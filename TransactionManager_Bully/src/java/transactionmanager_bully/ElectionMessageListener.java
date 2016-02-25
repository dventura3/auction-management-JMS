/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_bully;

import common.TransactionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import org.util.MsgPropertyID;

/**
 * MessageListener implementation used to receive the election message
 * @author marcx87
 */
public class ElectionMessageListener implements MessageListener {
        private TopicPublisher electionPublisher;
        private BullyEngine bullyEngine;
        private TextMessage abortMessage;

        public ElectionMessageListener(TopicPublisher electionPublisher, BullyEngine bullyEngine, TextMessage abortMessage){
            this.bullyEngine = bullyEngine;
            this.electionPublisher = electionPublisher;
            this.abortMessage = abortMessage;
        }

        @Override
        public void onMessage(Message message) {
                try {
                        if (message.getStringProperty(MsgPropertyID.Source).compareTo(TransactionManager.getClientID())<0) {
                                // The election message has been sent by a client with
                                // ID lower than this client. It is therefore to stop the
                                // election replying to the message
                                System.out.println("ELECTION message received from Client " + message.getStringProperty(MsgPropertyID.Source) + " (lower ID)");
                                abortMessage.setStringProperty(MsgPropertyID.Destination, message.getStringProperty(MsgPropertyID.Source));
                                electionPublisher.publish(abortMessage);
                                System.out.println("ABORT message sent.");
                                // Start a new election if not done previously
                                synchronized (bullyEngine) {
                                        if (TransactionManager.getInhibitElection() == false) {
                                                System.out.println("Initiating an election");
                                                // Set the election inhibition to true
                                                TransactionManager.setInhibitElection(true);
                                                new Thread(bullyEngine.getElection()).start();
                                        }
                                }
                        } else {
                                synchronized (bullyEngine) {
                                        System.out.println("Election message received from Client " + message.getStringProperty(MsgPropertyID.Source) + " (higher ID)");
                                        // Set the election inhibition to true
                                        TransactionManager.setInhibitElection(true);
                                }
                        }
                } catch (JMSException ex) {
                       Logger.getLogger(ElectionMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
}