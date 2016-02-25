/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author Daniela
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import org.dto.*;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;
import org.util.Pair;

public class AsWorkflowResponce implements Runnable{

    private TopicConnectionFactory connectionFactory;
    private Topic topic;
    private TopicConnection connection;
    private TopicSession subSession;
    private TopicSubscriber subscriber;
    private TopicPublisher publisher;
    private ListenerAckWorkflow listener;
    private String IDclient;
    private ObjectMessage objMessage;
    private static HashMap<Integer,String> wfHash;
    private DefaultListModel model2;

    public AsWorkflowResponce(TopicConnectionFactory connectionFactory,Topic topic,DefaultListModel model2){
        try {
            this.IDclient = MiddlewareMain.IDclient;
            this.model2 = model2;
            wfHash = new HashMap<Integer, String>();
            this.connectionFactory = connectionFactory;
            this.topic = topic;
            connection = connectionFactory.createTopicConnection();
            connection.setClientID(this.IDclient);
            subSession = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);
            this.objMessage = subSession.createObjectMessage();
            publisher = subSession.createPublisher(topic);
            subscriber = subSession.createSubscriber(topic,MsgPropertyID.Type + " = " + MessageTYPE.WorkflowResponse,true);
            this.listener = new ListenerAckWorkflow();
            subscriber.setMessageListener(this.listener);
            connection.start();
        } catch (JMSException ex) {
            Logger.getLogger(AsWorkflowResponce.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(3000);
                if(!wfHash.isEmpty()){
                    //mando un messaggio
                    sendIsAlive();
                    //verifico la risposta arrivata
                    synchronized(MiddlewareMain.sync){
                        while(ListenerAckWorkflow.result.getSecond()==null){
                            System.out.println("Sono bloccato ASWR");
                            MiddlewareMain.sync.wait();
                        }

                        String tipoMessaggio = ListenerAckWorkflow.result.getSecond();
                        int idWF = ListenerAckWorkflow.result.getFirst();

                        if(tipoMessaggio.equals("Ack"))
                        {
                            System.out.println("Tasks inviati con successo");
                            JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                                "Tasks inviati con successo",
                                "Result",
                                JOptionPane.INFORMATION_MESSAGE);
                            removeWorkflow(idWF);
                            model2.addElement(idWF + " - " + "Ok");
                        }else if(tipoMessaggio.equals("Nack")){
                            JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                               "Errore nel sottoporre il workflow a slavataggio",
                               "Result",
                               JOptionPane.ERROR_MESSAGE);
                            removeWorkflow(idWF);
                            model2.addElement(idWF + " - " + "Error");
                        }

                        ListenerAckWorkflow.result= new Pair<Integer, String>(0, null);
                        MiddlewareMain.sync.notifyAll();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(AsWorkflowResponce.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendIsAlive(){
        try {
            if(!wfHash.isEmpty()){
                Set<Integer> keys = wfHash.keySet();
                Iterator<Integer> it = keys.iterator();
                while(it.hasNext()){
                    int key = it.next();
                    String s = String.valueOf(key);
                    ResponseMessage response = new ResponseMessage();
                    response.setMessage(s);
                    //aggiorno il VectroClock
                    MiddlewareMain.incrementVectorClock(MiddlewareMain.vc);
                    //setto il vectroClock dentro il response
                    response.setVectorClock(MiddlewareMain.vc);
                    //salvo il vectorClock nel file di LOG
                    MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
                    //mando il messaggio
                    objMessage.setObject(response);
                    objMessage.setJMSType("IsAlive");
                    objMessage.setStringProperty(MsgPropertyID.Destination, wfHash.get(key));
                    objMessage.setStringProperty(MsgPropertyID.Source, MiddlewareMain.IDclient);
                    publisher.publish(objMessage);
                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(AsWorkflowResponce.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AsWorkflowResponce.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized void insertNewWorkflow(int idWF, String idManager){
        wfHash.put(idWF,idManager);
    }

    public static synchronized int removeWorkflow(int idWF){
        if(wfHash.containsKey(idWF)){
            wfHash.remove(idWF);
            return idWF;
        }
        return 0;
    }

    public static synchronized boolean containsWorkflow(int idWF){
        return wfHash.containsKey(idWF);
    }

}
