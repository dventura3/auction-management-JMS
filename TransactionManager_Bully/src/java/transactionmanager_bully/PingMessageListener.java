/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_bully;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 * MessageListener implementation used to reply to pings
 * @author marcx87
 */
public class PingMessageListener implements MessageListener {

        private TopicSession session;
        private TopicPublisher publisher;

        public PingMessageListener(TopicConnection connection, Topic topic) {
            super();
            try {
                session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                publisher = session.createPublisher(topic);
            } catch (JMSException ex) {
                Logger.getLogger(PingMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onMessage(Message message) {
            try {
                // Creating and sending reply
                String pongMessage = "PONG";
                TextMessage pong = session.createTextMessage();
                pong.setIntProperty(MsgPropertyID.Type, MessageTYPE.Pong);
                pong.setStringProperty(MsgPropertyID.Destination, message.getStringProperty(MsgPropertyID.Source));
                pong.setText(pongMessage);
                publisher.send(pong);
            } catch (JMSException ex) {
                Logger.getLogger(PingMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}