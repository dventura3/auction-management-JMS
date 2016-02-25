/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TMQueueManager;

import common.TransactionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import org.dto.QueueItemDTO;
import org.dto.ResponseMessage;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 *
 * @author marcx87
 */
public class AckManager implements Runnable{

    private volatile boolean acked;
    private TopicSession session;
    private ObjectMessage ackMessage;
    private ObjectMessage msg;
    private ObjectMessage arrivedMessage;
    private TopicPublisher publisher;

    public AckManager(TopicConnection connection, Topic topic) {
        try {
            this.session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            this.publisher = session.createPublisher(topic);
            ackMessage = session.createObjectMessage();
            connection.start();
        } catch (JMSException ex) {
            Logger.getLogger(AckManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
       synchronized(TransactionManager.sync){
           setAcked(false);
           QueueItemDTO item = null;
           try{
                if(this.arrivedMessage != null){
                    item = (QueueItemDTO) arrivedMessage.getObject();
                    TransactionManager.addQueueItem(item);
                }
                publisher.publish(msg);

                Thread.sleep(1500);
                if(isAcked() && this.arrivedMessage != null){
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("Ack");
                        TransactionManager.incrementVectorClock(item);
                        responseMessage.setVectorClock(TransactionManager.getVectorClock());
                        ackMessage.setObject(responseMessage);
                        ackMessage.setStringProperty(MsgPropertyID.Destination, arrivedMessage.getStringProperty(MsgPropertyID.Source));
                        ackMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.AckManager);
                        publisher.publish(ackMessage);
                        TransactionManager.sync.notifyAll();
                } else if(!isAcked() && this.arrivedMessage != null){
                    System.err.println("Sono l'unico TM sulla rete...");
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Ack");
                    TransactionManager.incrementVectorClock(item);
                    responseMessage.setVectorClock(TransactionManager.getVectorClock());
                    ackMessage.setObject(responseMessage);
                    ackMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.AckManager);
                    ackMessage.setStringProperty(MsgPropertyID.Destination, arrivedMessage.getStringProperty(MsgPropertyID.Source));
                    publisher.publish(ackMessage);
                    TransactionManager.sync.notifyAll();
                } else if(isAcked() && this.arrivedMessage == null){
                    if(msg.getJMSType().equals("Remove")){
                        TransactionManager.removeFirstItem();
                    }
                } else{
                     if(msg.getJMSType().equals("Remove")){
                        System.err.println("Sono l'unico TM sulla rete...(Remove)");
                        TransactionManager.removeFirstItem();
                    }
                }

            } catch (JMSException ex) {
                Logger.getLogger(AckManager.class.getName()).log(Level.SEVERE, null, ex);
            }catch (InterruptedException ex) {
                    Logger.getLogger(AckManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    public void setAcked(boolean acked){
        this.acked = acked;
    }

    public boolean isAcked(){
        return this.acked;
    }

    public void setBackupMessage(ObjectMessage ob, String operation){
        synchronized(TransactionManager.sync){
            try {
                System.out.println("SetBackupMessage; Operation: " + operation);
                this.msg = ob;
                this.msg.setJMSType(operation);
            } catch (JMSException ex) {
                Logger.getLogger(AckManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setArrivedMessage(ObjectMessage message){
        synchronized(TransactionManager.sync){
            this.arrivedMessage = message;
        }
    }
}
