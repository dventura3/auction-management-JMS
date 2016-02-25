/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package replicamanager;

/**
 *
 * @author Luigi Sinatra, Marco Messina, Daniela Ventura
 */
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import org.dto.QueueItemDTO;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

//Classe che gestisce il lato Publisher con l'inizializzazione del contesto JNDI, 
//delle connessioni e delle sessioni
public class PublisherLogic implements IPublisherLogic,ExceptionListener{
  private TopicConnection topicConnection;
  private TopicPublisher topicPublisher;
  private TopicSession pubSession;
  private TextMessage textMessage,rcvdMessage; //Messaggi pubblicati e ricevuti dal publisher
  private Publisher publisher;
  private static PublisherLogic instance=null;
  private String managerId;


    public PublisherLogic(TopicConnectionFactory tcf, Topic topic, String managerId) {
        try {
            this.managerId = managerId;
            //Inizializzazione del contesto JNDI
            topicConnection = tcf.createTopicConnection();
            //Creazione di un oggetto Sessione con modalità di ricezione settato ad AUTO_ACKNOWLEDGE
            pubSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //Crea il publisher per il topic in esame
            topicPublisher = pubSession.createPublisher(topic);
            //Modalità di consegna settato a persistente che assicura che i messaggi non vengano persi in caso di caduta del JMS provider
            topicPublisher.setDeliveryMode(DeliveryMode.PERSISTENT);
            publisher = new Publisher(topicPublisher);
            topicConnection.start();
        }
        catch (JMSException ex) {
            Logger.getLogger(PublisherLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public boolean publish(int type, String source)
    {
        try {
            //Crea un textMessage
            textMessage = pubSession.createTextMessage();
            //textMessage.setIntProperty("ID",transactionId);
            textMessage.setStringProperty(MsgPropertyID.Destination, source);
            textMessage.setIntProperty(MsgPropertyID.TransMessage, MessageTYPE.Transaction);
        }
        catch (JMSException ex) {
            Logger.getLogger(PublisherLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(publisher!=null)
        {
            switch(type){
                case 1:
                    System.out.println("Join");
                    return publisher.join(managerId,textMessage);
                case 2:
                    System.out.println("Ready");
                    return publisher.ready(managerId,textMessage);
                case 3:
                    System.out.println("NonReady");
                    return publisher.nonReady(managerId,textMessage);
                case 4:
                    System.out.println("Ack");
                    return publisher.ack(managerId,textMessage);
            }
        }
        return false;
    }

    public boolean publishQueueStatus(ArrayList<QueueItemDTO> array, String source)
    {
        try {
            ObjectMessage objMessage = pubSession.createObjectMessage();
            objMessage.setObject(array);
            objMessage.setStringProperty(MsgPropertyID.Destination, source);
            objMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.QueueStatus);
            objMessage.setIntProperty(MsgPropertyID.TransMessage, MessageTYPE.Transaction);
            objMessage.setStringProperty(MsgPropertyID.Source, managerId);
            return publisher.queueStatus(objMessage);
        } catch (JMSException ex) {
            Logger.getLogger(PublisherLogic.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    //Meccanismo di callback per segnalare eventuali problemi
    @Override
    public void onException(JMSException ex){
        System.out.println("JMS Exception: " + ex.getMessage());
    }

    //Utility
    @Override
    public TopicConnection getConnection(){
        return topicConnection;
    }

    @Override
    public Publisher getPublisher(){
        return publisher;
    }

    public static PublisherLogic getInstance(TopicConnectionFactory tcf, Topic topic, String managerId){

        if(instance==null){
            instance = new PublisherLogic(tcf, topic, managerId);
        }
        return instance;
    }

    @Override
    public TextMessage getMessage(){
        if(rcvdMessage!=null) return rcvdMessage;
        else return null;
    }

}