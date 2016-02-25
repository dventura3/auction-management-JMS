/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package replicamanager;
/**
 * Classe base che realizza tutti i subscriber
 * @author Sinatra Luigi, Messina Marco, Ventura Daniela
 */
// JNDI imports
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
// JMS imports
import javax.jms.*;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;
import org.dto.QueueItemDTO;
import org.dto.RequestTYPE;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;
import org.util.Pair;
import org.util.TwoPCState;

public class Subscriber implements ISubscriber,MessageListener,ExceptionListener,Runnable
{
    private TopicConnection topicConnection;
    private TopicSubscriber topicSubscriber;
    private TopicSession subSession;
    private Topic topic;
    private String managerId;
    private MessageService ms;
    private QueueManagerDAOImpl queueManager;
    private QueueItemDTO item;
    private String transactionId;
  
    public Subscriber(TopicConnectionFactory tcf, Topic topic, String Id)
    {
        try {
            topicConnection = tcf.createTopicConnection();
            this.topic = topic;
        } catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.managerId = Id;
        ms = new MessageService(tcf, topic, managerId );
        queueManager = new QueueManagerDAOImpl();
        try {
            //leggiamo il file di log e vediamo se avevamo delle operazioni da fare
            System.out.println("Leggiamo il file di log");
            Pair<QueueItemDTO, TwoPCState> p =queueManager.getLogData(managerId);
            //Se l’ultimo record nel log è azione o abort -> le azioni vanno disfatte (undo)
            if(p!=null)
            {
                if(p.getSecond().equals(TwoPCState.Abort))
                {
                    System.out.println("Abbiamo un'operazione di Abort da eseguire");
                    if(p.getFirst().getRequestType().toString().equals(RequestTYPE.Enqueue.toString()))
                    {
                        System.out.println("Facciamo un clear, poichè avevamo una Enqueue");
                        queueManager.clearQueueItemData(p.getFirst(), managerId, true);
                    }
                    else if(p.getFirst().getRequestType().toString().equals(RequestTYPE.Dequeue.toString()))
                    {
                        System.out.println("Facciamo uno store, poichè avevamo una Dequeue");
                        queueManager.StoreSingleQueueData(p.getFirst(), managerId, true);
                    }
                    else
                    {
                        System.out.println("Problema grosso, abbiamo saltato i primi if ed else if dell'Abort trovato nel log");
                    }
                }
                //Se l’ultimo record nel log è commit -> le azioni vanno rifatte (redo)
                else if (p.getSecond().equals(TwoPCState.Commit))
                {
                    System.out.println("Abbiamo un'operazione di Commit da eseguire");
                    if(p.getFirst().getRequestType().toString().equals(RequestTYPE.Enqueue.toString()))
                    {
                        System.out.println("Facciamo uno store, poichè avevamo una Enqueue");
                        queueManager.StoreSingleQueueData(p.getFirst(), managerId, true);
                    }
                    else if(p.getFirst().getRequestType().toString().equals(RequestTYPE.Dequeue.toString()))
                    {
                        System.out.println("Facciamo un clear, poichè avevamo una Dequeue");
                        queueManager.clearQueueItemData(p.getFirst(), managerId, true);
                    }
                }
                //Se l’ultimo record nel log è READY -> RM è in dubbio (è nell’intervallo di
                //incertezza) -> deve chiedere al TM l’esito finale della transazione
                else if(p.getSecond().equals(TwoPCState.Ready) || p.getSecond().equals(TwoPCState.NotReady))
                {
                    System.out.println("Nel log abbiamo trovato un Ready message o un NotReady, pertanto dobbiamo aspettare la GlobalDecision del TM");
                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run(){
        setupClient();
    }

    //Inizializza il subscriber
    public void setupClient(){
        try {
            //topicConnection.start();
            //pubSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            subSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //topicSubscriber = subSession.createDurableSubscriber(topic,managerId,null,true);
            topicSubscriber = subSession.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.Repliche, true);
            topicSubscriber.setMessageListener(this);   //Listener per il meccanismo di callback
            topicConnection.start();
        }
        catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
  //Meccanismo di callback: agisce durante la ricezione di un messaggio
    @Override
    public void onMessage(Message message){
        try {
            //Verifica che sia un messaggio di testo
            if (message instanceof TextMessage)
            {
                TextMessage rcvdMessage = (TextMessage) message;
                //Ricava il testo del messaggio
                String text = rcvdMessage.getText();
                String type = rcvdMessage.getJMSType();
                String source = rcvdMessage.getStringProperty(MsgPropertyID.Source);
                if(type.equals("RequestJoin"))
                {
                    System.out.println("Arrivata RequestJoin");
                    this.transactionId = text;
                    System.out.println("Inviamo la join");
                    this.join(source);
                }
                else if(type.equals("GlobalDecision"))
                {
                    if(rcvdMessage.getStringProperty("ID").equals(this.transactionId))
                    {
                        System.out.println("Arrivata GlobalDecision per la nostra transazione");
                        this.ack(text,source);
                    }
                }
            }
            else if(message instanceof ObjectMessage)
            {
                ObjectMessage rcvdMessage = (ObjectMessage) message;
                String type = rcvdMessage.getJMSType();
                String source = rcvdMessage.getStringProperty(MsgPropertyID.Source);
                this.item = (QueueItemDTO) rcvdMessage.getObject();
                if(type!=null){
                    if(type.equals("Prepare"))
                    {
                        if(rcvdMessage.getStringProperty("ID").equals(this.transactionId))
                        {
                            if(item.getRequestType().toString().equals(RequestTYPE.Dequeue.toString())
                                    || item.getRequestType().toString().equals(RequestTYPE.Enqueue.toString()))
                            {
                            //facciamo il commit
                                System.out.println("Arrivato messaggio di Prepare");
                                this.sendReadyMsg(item, source);
                            }
                        }
                    }
                }
                else if(item.getRequestType().toString().equals(RequestTYPE.QueueStatus.toString()))
                    this.sendQueueStatus(item,source);
            }
        }
        catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void join(String source){
        if(ms.sendMessage(1, source))
        {
            System.out.println("Join inviata con successo a: " + source);
        }
    }

    private void sendReadyMsg(QueueItemDTO item, String source){
        try{
            if (item.getRequestType().equals(RequestTYPE.Enqueue))
            {
                this.queueManager.StoreSingleQueueData(item, managerId, true);
                System.out.println("Enqueue avvenuto con successo, inviamo il messaggio di Commit");
            }
            else if (item.getRequestType().equals(RequestTYPE.Dequeue))
            {
                this.queueManager.clearQueueItemData(item, managerId, true);
                System.out.println("Dequeue avvenuta con successo, inviamo il messaggio di Commit");
            }
            this.queueManager.LogSingleQueueData(item, TwoPCState.Ready, managerId);
            ms.sendMessage(2, source);
        }
        catch(Exception e){
            //facciamo l'abort
            System.out.println("Eccezione nel salvataggio dei dati nel file XML, inviamo Abort");
            try{
                this.queueManager.LogSingleQueueData(item, TwoPCState.NotReady, managerId);
                ms.sendMessage(3, source);
            }
            catch(JAXBException ex){
                System.out.println("JAXBException nel salvataggio dei dati nel file XML, inviamo Abort");
            }
            catch(IOException ex){
                System.out.println("IOException nel salvataggio dei dati nel file XML, inviamo Abort");
            }
        }
    }

    private void ack(String text, String source){
        if(text.equals("COMMIT")){
            System.out.println("Arrivata GlobalDecision: COMMIT");
            try{
                //salviamolo
                this.queueManager.LogSingleQueueData(item, TwoPCState.Commit, managerId);
//                Pair<QueueItemDTO, TwoPCState> p = queueManager.getLogData(managerId);
//                this.queueManager.StoreSingleQueueData(p.getFirst(),managerId);
                if(item.getRequestType().toString().equals(RequestTYPE.Enqueue.toString())){
                    this.queueManager.StoreSingleQueueData(item, managerId, true);
                } else if(item.getRequestType().toString().equals(RequestTYPE.Dequeue.toString())){
                    this.queueManager.clearQueueItemData(item, managerId, true);
                }
                //invio l'ack dell'avvenuto commit
                System.out.println("Inviamo ACK al TM");
                if(ms.sendMessage(4,source)){
                    System.out.println("Invio ack completato");
                    this.queueManager.clearLogData(managerId);
                }
            }
            catch(JAXBException e){
                System.out.println("JAXBException nel salvare i dati nel metodo ack");
            }
            catch(IOException e){
                System.out.println("IOException nel salvare i dati nel metodo ack");
            }
        }
        else{
            //faccio l'abort sui dati appena scritti
            System.out.println("Arrivata GlobalDecision: ABORT");
            try{
                //Pair<QueueItemDTO, TwoPCState> p = queueManager.getLogData(managerId);
                //cancelliamo il QueueItemDTO appena trovato
                //this.queueManager.clearQueueItemData(p.getFirst(),managerId);
                this.queueManager.LogSingleQueueData(item, TwoPCState.Abort, managerId);
                if(item.getRequestType().toString().equals(RequestTYPE.Enqueue.toString()))
                {
                    System.out.println("Facciamo la clear, dato che prima abbiamo fatto enqueue");
                    this.queueManager.clearQueueItemData(item, managerId, true);
                }
                else if (item.getRequestType().toString().equals(RequestTYPE.Dequeue.toString()))
                {
                    System.out.println("Facciamo la store, dato che prima abbiamo fatto dequeue");
                    this.queueManager.StoreSingleQueueData(item, managerId, true);
                }
                //invio l'ack dell'avvenuto abort
                System.out.println("Inviamo ACK al TM");
                if(ms.sendMessage(4, source)){
                    System.out.println("Invio ack completato");
                    this.queueManager.clearLogData(managerId);
                }
            }
            catch(JAXBException e){
                System.out.println("JAXBException nel salvare i dati nel metodo ack");
            }
            catch(IOException e){
                System.out.println("IOException nel salvare i dati nel metodo ack");
            }
        }
    }

    private void sendQueueStatus(QueueItemDTO item, String source) {
        try 
        {
            this.ms.sendQueueStatus(queueManager.getAllQueueStoreData(item, managerId), source);
        } 
        catch(JAXBException e){
            System.out.println("JAXBException nel send queue status");
        }
        catch(IOException e){
            System.out.println("IOException nel send queue status");
        }
        catch(ParseException e){
            System.out.println("Parse exception nel send queue status");
        }
    }
    //Utility
    @Override
    public TopicConnection getTopicConnection(){
        return topicConnection;
    }
    @Override
    public TopicSession getSession(){
        return subSession;
    }
    @Override
    public TopicSubscriber getSubscriber(){
        return topicSubscriber;
    }
    public String getManagerId(){
        return managerId;
    }
  //Verifica se il subscriber è connesso o meno
    @Override
    public boolean isConnected(){
        try{
            topicConnection.getClientID();
            return true;
        }
        catch (JMSException e){
            return false;
        }
    }
    @Override
    public void onException(JMSException ex) {
        Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
    }
}


