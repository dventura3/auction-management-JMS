/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import org.dto.ResponseMessage;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 *
 * @author Daniela
 */
public class DequeueListener implements Callable {
    
    private TopicSession session;
    private TopicPublisher publisher;
    private TopicSubscriber ackSubscriber;
    private ObjectMessage objectArrived; //msg ricevuto

    public DequeueListener(TopicConnection connection, Topic topic){
        try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            publisher = session.createPublisher(topic);
            ackSubscriber = session.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.TransactionDequeue + " AND " + MsgPropertyID.Destination + " = '" + MainManager.nomeManager +"'", true);
        } catch (JMSException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer call() throws Exception {
        objectArrived = (ObjectMessage) ackSubscriber.receive();
        ResponseMessage response = (ResponseMessage)objectArrived.getObject();
        String result = response.getMessage();
        if(result.equals("OK")){
            return 1;
        }
        return 0;
    }

}
