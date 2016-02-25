/*
 * Classe che ralizza il publisher 
 */

package replicamanager;

/**
 *
 * @author  Luigi Sinatra, Marco Messina, Daniela Ventura
 */

// JMS imports
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;

//Classe che realizza il Publisher
public class Publisher
{
    
    private TopicPublisher topicPublisher;

    public Publisher(TopicPublisher tp)
    {
        this.topicPublisher = tp;
    }
  
  //Metodo per la pubblicazione del Join nel topic
    public boolean join(String managerId, TextMessage textMessage)
    {
        try {
            textMessage.setText(managerId);
            textMessage.setJMSType("Join");
            topicPublisher.publish(textMessage,DeliveryMode.NON_PERSISTENT,4,Message.DEFAULT_TIME_TO_LIVE);
            return true;
        }
        catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean ready(String managerId, TextMessage textMessage)
    {
        try{
            textMessage.setJMSType("Ready");
            textMessage.setText(managerId+"#"+1);
            topicPublisher.publish(textMessage);
            return true;
        }
        catch(JMSException e)
        {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean nonReady(String managerId, TextMessage textMessage)
    {
        try{
            textMessage.setJMSType("Ready");
            textMessage.setText(managerId+"#"+2);
            topicPublisher.publish(textMessage);
            return true;
        }
        catch(JMSException e)
        {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean ack(String managerId,TextMessage textMessage)
    {
        System.out.println("Provo a mandare il mio ack");
        try{
            textMessage.setText(managerId);
            textMessage.setJMSType("Ack");
            topicPublisher.publish(textMessage,DeliveryMode.NON_PERSISTENT,4,Message.DEFAULT_TIME_TO_LIVE);
            return true;
        }
        catch(JMSException e){
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean queueStatus(ObjectMessage objMessage)
    {
        try {
            objMessage.setJMSType("QueueStatus");
            topicPublisher.publish(objMessage);
            return true;
        }
        catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}