/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_2pc;

import TMQueueManager.AckManager;
import common.TransactionManager;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.TopicSubscriber;
import java.util.Timer;
import java.util.TimerTask;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.xml.bind.JAXBException;
import org.dto.QueueItemDTO;
import org.dao.*;
import org.dto.ManagerStatus;
import org.dto.RequestTYPE;
import org.dto.ResponseMessage;
import org.dto.TMStatus;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;
import org.util.Pair;
import org.util.TwoPCState;

/**
 *
 * @author Luigi Sinatra, Marco Messina, Daniela Ventura
 */
public class TwoPCEngine implements Runnable {

    //private static int MAX_N_REPETIONS = 10;
    private TopicSubscriber topicSubscriber;
    private TopicPublisher topicPublisher;
    private TopicSession transSession;
    private String IDQueue;
    private HashMap<String,Integer> replicaManagers;
    private Timer timeout1;
    private Timer timeout2;
    private TimerTask timertask;
    private String GLOBAL_DECISION;
    private QueueManagerDAOImpl qmdao;
    private int res;
    private QueueItemDTO qidto;
    //private int repetitionTimes;
    private boolean timeToWaitReadyMessagesElapsed;
    private boolean timeToReceiveAckElapsed;
    private String transactionId;
    private ArrayList<ArrayList<QueueItemDTO>> listOfResponse;
    private AckManager ackManager;
    private boolean firstTimeRunning;
    
    public TwoPCEngine(TopicConnection connection, Topic topic, AckManager ackManager)
    {
        timeout1 = new Timer();
        timeout2 = new Timer();
        qmdao = new QueueManagerDAOImpl();
        replicaManagers = new HashMap<String, Integer>();
        listOfResponse = new ArrayList<ArrayList<QueueItemDTO>>();
        res = 0;
        this.ackManager = ackManager;
        firstTimeRunning = true;
        try {
            transSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicSubscriber = transSession.createSubscriber(topic,MsgPropertyID.TransMessage + " = " + MessageTYPE.Transaction + " AND " + MsgPropertyID.Destination + " = '" + TransactionManager.getClientID() + "'",true);
            topicPublisher = transSession.createPublisher(topic);
            topicSubscriber.setMessageListener(new TwoPCMessageListener(this,transSession));
        } catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while(true)
        {
//            synchronized(TransactionManager.sync)
//            {
                if(firstTimeRunning)
                {
                    try {
                        Pair<QueueItemDTO, TwoPCState> p = qmdao.getLogData(TransactionManager.getClientID());
                        if(p!=null)
                        {
                            qidto = p.getFirst();
                            transactionId = p.getFirst().getID() + p.getFirst().getRequestType() + p.getFirst().getWorkflow().getID();
                            if(p.getSecond().equals(TwoPCState.Prepare))
                            {
                                System.out.println("Nel Log c'era Prepare, per cui mandiamo GlobalAbort");
                                Iterator<String> it = p.getFirst().getReplicaManager().listIterator();
                                while(it.hasNext())
                                {
                                    this.replicaManagers.put(it.next(), null);
                                }
                                setGLOBAL_DECISION("ABORT");
                                sendGlobalDecision();
                            }
                            else if(p.getSecond().equals(TwoPCState.Commit))
                            {
                                Iterator<String> it = p.getFirst().getReplicaManager().listIterator();
                                while(it.hasNext())
                                {
                                    this.replicaManagers.put(it.next(), 1);
                                }
                                System.out.println("Nel Log c'era Commit, per cui mandiamo GlobalCommit");
                                setGLOBAL_DECISION("COMMIT");
                                sendGlobalDecision();
                            }
                            else if(p.getSecond().equals(TwoPCState.Abort))
                            {
                                Iterator<String> it = p.getFirst().getReplicaManager().listIterator();
                                while(it.hasNext())
                                {
                                    this.replicaManagers.put(it.next(), 2);
                                }
                                System.out.println("Nel Log c'era Abort, per cui mandiamo GlobalAbort");
                                setGLOBAL_DECISION("ABORT");
                                sendGlobalDecision();
                            }
                        }
                    } catch (JAXBException ex) {
                        Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParseException ex) {
                        Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    firstTimeRunning = false;
                }
                else
                {
                    synchronized (TransactionManager.sync){
                        qidto = TransactionManager.getFirstItem();
                        if(qidto == null)
                        {
                            try {
                                TransactionManager.sync.wait();
                                qidto = TransactionManager.getFirstItem();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    if(qidto != null && TransactionManager.getUpdatedStatus())
                    {
                        if(qidto.getRequestType().toString().equals(RequestTYPE.Enqueue.toString())
                                || qidto.getRequestType().toString().equals(RequestTYPE.Dequeue.toString()))
                        {
                            System.out.println("Arrivata richiesta di transazione");
                            transactionId = qidto.getID() + qidto.getRequestType() + qidto.getWorkflow().getID();
                            setReplicaManagers();
                            System.out.println("ReplicaManagers settati");
                            if(!this.replicaManagers.isEmpty())
                            {
                                sendPrepare();
                                System.out.println("Prepare inviata");
                                //TransactionManager.removeFirstItem();
                                try {
                                    ObjectMessage obj = transSession.createObjectMessage();
                                    if (qidto.getRequestType().toString().equals(RequestTYPE.Enqueue.toString())) {
                                        obj.setIntProperty(MsgPropertyID.Type, MessageTYPE.TransactionEnqueue);
                                    } else if (qidto.getRequestType().toString().equals(RequestTYPE.Dequeue.toString())) {
                                        obj.setIntProperty(MsgPropertyID.Type, MessageTYPE.TransactionDequeue);
                                    }
                                    obj.setStringProperty(MsgPropertyID.Destination, qidto.getID());
                                    obj.setIntProperty("Workflow", qidto.getWorkflow().getID());
                                    ResponseMessage rm = new ResponseMessage();
                                    if (result() == 1)
                                        rm.setMessage("OK");
                                    else
                                        rm.setMessage("ERROR");
                                    TransactionManager.incrementVectorClock(qidto);
                                    rm.setVectorClock(TransactionManager.getVectorClock());
                                    obj.setObject(rm);
                                    topicPublisher.publish(obj);
                                }
                                catch (JMSException ex) {
                                    Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else
                            {
                                System.out.println("Non mi ha risposto nessuna replica, transazione fallita!");
                                try {
                                    ObjectMessage obj = transSession.createObjectMessage();
                                    if (qidto.getRequestType().toString().equals(RequestTYPE.Enqueue.toString())) {
                                        obj.setIntProperty(MsgPropertyID.Type, MessageTYPE.TransactionEnqueue);
                                    } else if (qidto.getRequestType().toString().equals(RequestTYPE.Dequeue.toString())) {
                                        obj.setIntProperty(MsgPropertyID.Type, MessageTYPE.TransactionDequeue);
                                    }
                                    obj.setStringProperty(MsgPropertyID.Destination, qidto.getID());
                                    obj.setIntProperty("Workflow", qidto.getWorkflow().getID());
                                    ResponseMessage rm = new ResponseMessage();
                                    rm.setMessage("ERROR");
                                    TransactionManager.incrementVectorClock(qidto);
                                    rm.setVectorClock(TransactionManager.getVectorClock());
                                    obj.setObject(rm);
                                    topicPublisher.publish(obj);
                                }
                                catch (JMSException ex) {
                                    Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            try{
                                ObjectMessage statusMessage = this.transSession.createObjectMessage();
                                statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
                                statusMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
                                TransactionManager.incrementVectorClock(qidto);
                                TMStatus status = new TMStatus();
                                status.setVectorClock(TransactionManager.getVectorClock());
                                Pair<QueueItemDTO, TwoPCState> map = this.qmdao.getLogData(TransactionManager.getClientID());
                                if(map != null)
                                {
                                    status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                                    status.setTwoPCState((TwoPCState) map.getSecond());
                                }
                                //qmdao.StoreSingleQueueData(qidto, TransactionManager.getClientID(), false);
                                Thread sendAck = new Thread(ackManager);
                                status.setBufferedItems(qmdao.getAllQueueData(TransactionManager.getClientID()));
                                statusMessage.setObject(status);
                                ackManager.setBackupMessage(statusMessage, "Remove");
                                ackManager.setArrivedMessage(null);
                                //TODO
                                //TransactionManager.sync.notifyAll();
                                sendAck.start();
                                sendAck.join();
                            }
                            catch (JMSException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            catch(JAXBException e)
                            {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, e);
                            }
                            catch(IOException ioe)
                            {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ioe);
                            }
                            catch(ParseException ioe)
                            {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ioe);
                            }
                            catch(InterruptedException e)
                            {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                        else if(qidto.getRequestType().toString().equals(RequestTYPE.QueueStatus.toString()))
                        {
                            try {
                                System.out.println("Arrivata richiesta per il QueueStatus");
                                this.sendRequestQueueStatus();
                                //TransactionManager.removeFirstItem();
                                ObjectMessage statusMessage = this.transSession.createObjectMessage();
                                statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
                                statusMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
                                TransactionManager.incrementVectorClock(qidto);
                                TMStatus status = new TMStatus();
                                status.setVectorClock(TransactionManager.getVectorClock());
                                Pair<QueueItemDTO, TwoPCState> map = this.qmdao.getLogData(TransactionManager.getClientID());
                                if (map != null) {
                                    status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                                    status.setTwoPCState((TwoPCState) map.getSecond());
                                }
                                //qmdao.StoreSingleQueueData(qidto, TransactionManager.getClientID(), false);
                                Thread sendAck = new Thread(ackManager);
                                status.setBufferedItems(qmdao.getAllQueueData(TransactionManager.getClientID()));
                                statusMessage.setObject(status);
                                ackManager.setBackupMessage(statusMessage, "Remove");
                                ackManager.setArrivedMessage(null);
                                sendAck.start();
                                //TransactionManager.sync.notifyAll();
                                sendAck.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (JAXBException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ParseException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (JMSException ex) {
                                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }

    private int result(){
        synchronized(this){
            try{
                wait();
            }catch(Exception e){
                Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return res;
    }

    public HashMap<String,Integer> getReplicaManagers(){
        return this.replicaManagers;
    }

    private void setReplicaManagers() {
        try {
            TextMessage msg = transSession.createTextMessage();
            msg.setIntProperty(MsgPropertyID.Type, MessageTYPE.Repliche);
            msg.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            msg.setJMSType("RequestJoin");
            msg.setText(transactionId);
            topicPublisher.publish(msg);
            System.out.println("Attendiamo 3 secondi l'arrivo dei Join");
            Thread.sleep(3000);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendPrepare() {
        try {
            System.out.println("Inviamo il messaggio di PREPARE");
            Iterator<String> it = this.replicaManagers.keySet().iterator();
            String[] a = new String[this.replicaManagers.size()];
            for (int i = 0; i < this.replicaManagers.size(); i++) {
                if (it.hasNext()) {
                    a[i] = it.next();
                }
            }
            this.qmdao.LogSingleQueueData(qidto, TwoPCState.Prepare, TransactionManager.getClientID(), a);
            System.out.println("Messaggio di Log salvato, lo mandiamo ai Backup");

            //Invio ai TM di Backup
            ObjectMessage statusMessage = this.transSession.createObjectMessage();
            statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
            statusMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            TransactionManager.incrementVectorClock(qidto);
            TMStatus status = new TMStatus();
            status.setVectorClock(TransactionManager.getVectorClock());
            Pair<QueueItemDTO, TwoPCState> map = this.qmdao.getLogData(TransactionManager.getClientID());
            if(map != null)
            {
                System.out.println("Map first: " + map.getFirst() + "Map second: "+ map.getSecond());
                status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                status.setTwoPCState((TwoPCState) map.getSecond());
            }
            Thread sendAck = new Thread(ackManager);
            status.setBufferedItems(qmdao.getAllQueueData(TransactionManager.getClientID()));
            statusMessage.setObject(status);
            ackManager.setBackupMessage(statusMessage, "Update");
            ackManager.setArrivedMessage(null);
            sendAck.start();
            sendAck.join();
          
            ObjectMessage objMessage = transSession.createObjectMessage();
            objMessage.setObject(qidto);
            objMessage.setJMSType("Prepare");
            objMessage.setStringProperty("ID", transactionId);
            objMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Repliche);
            objMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            topicPublisher.publish(objMessage, DeliveryMode.NON_PERSISTENT, 4, Message.DEFAULT_TIME_TO_LIVE);
            timeout1 = new Timer();
            timeout1.schedule(new TimerTask() {

                @Override
                public void run() {
                    //Indicare il Global Abort
                    System.out.println("Timer 1 scaduto, facciamo il GlobalAbort");
                    timeToWaitReadyMessagesElapsed = true;
                    GLOBAL_DECISION = "ABORT";
                    sendGlobalDecision();
                }
            }, 4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    }

    public void sendGlobalDecision(){
        try {
            System.out.println("inviamo la global decision");
            timeToReceiveAckElapsed = false;
            timeout1.cancel();
            timeout1.purge();
            timeToWaitReadyMessagesElapsed = false;
            timeout2.cancel();
            timeout2.purge();
            //QueueItemDTO dto = new QueueItemDTO(IDQueue, wf, new Date());
            Iterator<String> it = this.replicaManagers.keySet().iterator();
            String[] a = new String[this.replicaManagers.size()];
            for (int i = 0; i < this.replicaManagers.size(); i++) {
                if (it.hasNext()) {
                    a[i] = it.next();
                }
            }
            //Scrittura su LOG
            if (GLOBAL_DECISION.equals("COMMIT")) {
                    this.qmdao.LogSingleQueueData(qidto, TwoPCState.Commit, TransactionManager.getClientID(),a);

            } else {
                    this.qmdao.LogSingleQueueData(qidto, TwoPCState.Abort, TransactionManager.getClientID(),a);

            }

            //Invio ai TM di Backup
            ObjectMessage statusMessage = this.transSession.createObjectMessage();
            statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
            statusMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            TransactionManager.incrementVectorClock(qidto);
            TMStatus status = new TMStatus();
            status.setVectorClock(TransactionManager.getVectorClock());
            Pair<QueueItemDTO, TwoPCState> map = this.qmdao.getLogData(TransactionManager.getClientID());
            if(map != null)
            {
                status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                status.setTwoPCState((TwoPCState) map.getSecond());
            }
            //qmdao.StoreSingleQueueData(qidto, TransactionManager.getClientID(), false);
            Thread sendAck = new Thread(ackManager);
            status.setBufferedItems(qmdao.getAllQueueData(TransactionManager.getClientID()));
            statusMessage.setObject(status);
            ackManager.setBackupMessage(statusMessage, "Update");
            ackManager.setArrivedMessage(null);
            sendAck.start();
            sendAck.join();

            TextMessage textMessage = transSession.createTextMessage();
            textMessage.setText(GLOBAL_DECISION);
            textMessage.setJMSType("GlobalDecision");
            textMessage.setStringProperty("ID", transactionId);
            textMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Repliche);
            textMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            topicPublisher.publish(textMessage, DeliveryMode.NON_PERSISTENT, 4, Message.DEFAULT_TIME_TO_LIVE);
            if (GLOBAL_DECISION.equals("COMMIT")) {
                res = 1;
            } else {
                res = 0;
            }
            timeout2 = new Timer();
            //se scade il secondo timeout si deve ripetere la seconda fase all'infinito finchè non si
            //ricevono tutti gli ack
                timeout2.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        //Indicare il Global Abort
                        System.out.println("Timeout2 Scaduto");
                        timeToReceiveAckElapsed = true;
                        sendGlobalDecision();
                    }
                }, 5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch(JAXBException ex){
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex){
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void completeTransaction() {
        try {
            System.out.println("Transazione Completata");
            if (GLOBAL_DECISION.equals("COMMIT")) {
                res = 1;
            }
            else {
                res = 0;
            }
            timeout2.cancel();
            timeout2.purge();
            this.qmdao.LogSingleQueueData(qidto, TwoPCState.Complete, TransactionManager.getClientID());
            //Invio ai TM di Backup
            ObjectMessage statusMessage = this.transSession.createObjectMessage();
            statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
            statusMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            TransactionManager.incrementVectorClock(qidto);
            TMStatus status = new TMStatus();
            status.setVectorClock(TransactionManager.getVectorClock());
            Pair<QueueItemDTO, TwoPCState> map = this.qmdao.getLogData(TransactionManager.getClientID());
            if(map != null)
            {
                status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                status.setTwoPCState((TwoPCState) map.getSecond());
            }
            Thread sendAck = new Thread(ackManager);
            status.setBufferedItems(qmdao.getAllQueueData(TransactionManager.getClientID()));
            statusMessage.setObject(status);
            ackManager.setBackupMessage(statusMessage, "Update");
            ackManager.setArrivedMessage(null);
            sendAck.start();
            sendAck.join();

            this.replicaManagers.clear();
            this.qmdao.clearLogData(TransactionManager.getClientID());
        } catch (InterruptedException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        synchronized (this) {
            notifyAll();
        }
    }

    private void sendRequestQueueStatus() {
        try {
            ObjectMessage obj = transSession.createObjectMessage();
            obj.setObject(qidto);
            obj.setIntProperty(MsgPropertyID.Type, MessageTYPE.Repliche);
            obj.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            topicPublisher.publish(obj);
            System.out.println("Richiesta del queueStatus pubblicata alle repliche, attendiamo 3 secondi");
            //attendo 3 secondi le risposte delle repliche
            Thread.sleep(5000);
            sendResponseQueueStatus();
        }
        catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(InterruptedException e)
        {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void sendResponseQueueStatus()
    {
        TransactionManager.incrementVectorClock(qidto);
        Iterator<ArrayList<QueueItemDTO>> it = this.listOfResponse.listIterator();
        ArrayList<QueueItemDTO> itemSelected = null;
        if(this.listOfResponse.size() == 1)
        {
            itemSelected = this.listOfResponse.get(0);
        }
        else if((this.listOfResponse.size() > 1))
        {
            while(it.hasNext())
            {
                ArrayList<QueueItemDTO> tmp = it.next();
                for(int i = 0; i<this.listOfResponse.size(); i++)
                {
                    ArrayList<QueueItemDTO> comparer = this.listOfResponse.get(i);
                    if(tmp.equals(comparer))
                    {
                        itemSelected = tmp;
                    }
                    else
                    {
                        itemSelected = null;
                        break;
                    }
                }
            }
        }
        ManagerStatus status = new ManagerStatus();
        status.setVectorClock(TransactionManager.getVectorClock());
        if(itemSelected != null)
        {
            status.setItems(itemSelected);            
        }
        else
        {
            if(!this.listOfResponse.isEmpty())
            {
                status.setItems(this.listOfResponse.get(0));
            }
            else
                status.setItems(null);
        }
        System.out.println("Inviamo il messaggio con il queueStatus");
        try
        {
            ObjectMessage obj = transSession.createObjectMessage();
            obj.setIntProperty(MsgPropertyID.Type, MessageTYPE.QueueStatus);
            obj.setStringProperty(MsgPropertyID.Destination, qidto.getID());
            obj.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            obj.setObject(status);
            topicPublisher.publish(obj);
        }
        catch (JMSException ex) {
            Logger.getLogger(TwoPCEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //get e set
    public String getGLOBAL_DECISION() {
        return GLOBAL_DECISION;
    }

    public void setGLOBAL_DECISION(String GLOBAL_DECISION) {
        this.GLOBAL_DECISION = GLOBAL_DECISION;
    }

    public String getIDQueue() {
        return IDQueue;
    }

    public void setIDQueue(String IDQueue) {
        this.IDQueue = IDQueue;
    }

    public QueueItemDTO getQidto() {
        return qidto;
    }

    public void setQidto(QueueItemDTO qidto) {
        this.qidto = qidto;
    }

    public QueueManagerDAOImpl getQmdao() {
        return qmdao;
    }

    public void setQmdao(QueueManagerDAOImpl qmdao) {
        this.qmdao = qmdao;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public boolean isTimeToReceiveAckElapsed() {
        return timeToReceiveAckElapsed;
    }

    public void setTimeToReceiveAckElapsed(boolean timeToReceiveAckElapsed) {
        this.timeToReceiveAckElapsed = timeToReceiveAckElapsed;
    }

    public boolean isTimeToWaitReadyMessagesElapsed() {
        return timeToWaitReadyMessagesElapsed;
    }

    public void setTimeToWaitReadyMessagesElapsed(boolean timeToWaitReadyMessagesElapsed) {
        this.timeToWaitReadyMessagesElapsed = timeToWaitReadyMessagesElapsed;
    }

    public Timer getTimeout1() {
        return timeout1;
    }

    public void setTimeout1(Timer timeout1) {
        this.timeout1 = timeout1;
    }

    public Timer getTimeout2() {
        return timeout2;
    }

    public void setTimeout2(Timer timeout2) {
        this.timeout2 = timeout2;
    }

    public TimerTask getTimertask() {
        return timertask;
    }

    public void setTimertask(TimerTask timertask) {
        this.timertask = timertask;
    }

    public TopicPublisher getTopicPublisher() {
        return topicPublisher;
    }

    public void setTopicPublisher(TopicPublisher topicPublisher) {
        this.topicPublisher = topicPublisher;
    }

    public TopicSubscriber getTopicSubscriber() {
        return topicSubscriber;
    }

    public void setTopicSubscriber(TopicSubscriber topicSubscriber) {
        this.topicSubscriber = topicSubscriber;
    }

    public TopicSession getTransSession() {
        return transSession;
    }

    public void setTransSession(TopicSession transSession) {
        this.transSession = transSession;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public ArrayList<ArrayList<QueueItemDTO>> getListOfResponse() {
        return listOfResponse;
    }
}
