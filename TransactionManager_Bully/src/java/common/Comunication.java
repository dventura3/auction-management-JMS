/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;


import TMQueueManager.AckBackupMessageListener;
import TMQueueManager.AckManager;
import TMQueueManager.TMQueueManagerMessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;
import transactionmanager_2pc.TwoPCEngine;
import transactionmanager_bully.AbortMessageListener;
import transactionmanager_bully.BullyEngine;
import transactionmanager_bully.CoordinatorMessageListener;
import transactionmanager_bully.ElectionMessageListener;
import transactionmanager_bully.PingMessageListener;
/**
 * Classe che inizializza i canali di comunicazione con i vari elementi collegati al TM
 * @author marcx87
 */
public class Comunication {

        private TopicConnection connection;
        private TopicSession pongSession;
        private TopicSubscriber pongSubscriber;
        private TopicPublisher electionPublisher;
        private TopicPublisher currentStatusPublisher;
        private TopicSubscriber backupSubscriber;
        private TopicSession communicationSession;
        private TopicSession abortSession;
        private TopicSubscriber abortSubscriber;
        private TopicSubscriber coordinatorSubscriber;
        private TopicSubscriber electionReceiverSubscriber;
        private TopicSubscriber currentStatusReceiverSubscriber;
        private TopicSubscriber TMQueueManagerSubscriber;
        private TextMessage abortMessage;
        private ObjectMessage statusMessage;
        private BullyEngine bullyEngine;
        private AckManager ackManager;
        private TwoPCEngine twoPCEngine;

        public Comunication(TopicConnectionFactory tcf, Topic topic) {
            try {
                // Creating JMS objects
                System.out.println("Creating JMS objects...");
                connection = tcf.createTopicConnection();
                // Pong JMS objects (used to catch a ping and reply with a pong)
                pongSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                pongSubscriber = pongSession.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.Ping + " AND " + MsgPropertyID.Destination + " = '" + TransactionManager.getClientID()+"'", true);
                pongSubscriber.setMessageListener(new PingMessageListener(connection, topic));
                // JMS objects
                communicationSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                TMQueueManagerSubscriber = communicationSession.createSubscriber(topic, MsgPropertyID.Type +" = " + MessageTYPE.QueueManager, true);
                backupSubscriber = communicationSession.createSubscriber(topic, MsgPropertyID.Type +" = " + MessageTYPE.AckBackup + " AND " + MsgPropertyID.Destination + " = '" + TransactionManager.getClientID()+"'", true);
                electionPublisher = communicationSession.createPublisher(topic);
                electionReceiverSubscriber = communicationSession.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.Election, true);
                currentStatusPublisher = communicationSession.createPublisher(topic);
                currentStatusReceiverSubscriber = communicationSession.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.Status, true);
                currentStatusReceiverSubscriber.setMessageListener(new StatusMessageListener(connection, topic));
                coordinatorSubscriber = communicationSession.createSubscriber(topic, MsgPropertyID.Type + " =  " + MessageTYPE.Coordinator, true);
                // Creating engine thread
                bullyEngine = new BullyEngine(connection, topic);
                statusMessage = communicationSession.createObjectMessage();
                ackManager = new AckManager(connection, topic);
                TMQueueManagerSubscriber.setMessageListener(new TMQueueManagerMessageListener(ackManager, statusMessage));
                backupSubscriber.setMessageListener(new AckBackupMessageListener(ackManager));
                coordinatorSubscriber.setMessageListener(new CoordinatorMessageListener(bullyEngine, currentStatusPublisher, statusMessage));
                abortSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                abortSubscriber = abortSession.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.Abort + " AND " + MsgPropertyID.Destination + " = '" + TransactionManager.getClientID()+"'", true);
                abortSubscriber.setMessageListener(new AbortMessageListener(bullyEngine));
                abortMessage = communicationSession.createTextMessage();
                abortMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Abort);
                abortMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
                electionReceiverSubscriber.setMessageListener(new ElectionMessageListener(electionPublisher, bullyEngine, abortMessage));
                twoPCEngine = new TwoPCEngine(connection, topic, ackManager);
            } catch (JMSException ex) {
                Logger.getLogger(Comunication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * Starts the component.
         */
        public void start() {
            try {
                connection.start();
            } catch (JMSException ex) {
                Logger.getLogger(Comunication.class.getName()).log(Level.SEVERE, null, ex);
            }
                System.out.println("Starting BullyEngine");
                new Thread(bullyEngine).start();
                System.out.println("Starting 2PCEngine");
                new Thread(twoPCEngine).start();
        }
        /**
         * Stops the component.
         */
        public void stop() {
                // Stop the pinger thread
                bullyEngine.exit();
                // Close JMS objects
                try {
                        pongSubscriber.close();
                        connection.close();
                } catch (JMSException ex) {
                        Logger.getLogger(Comunication.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
}

