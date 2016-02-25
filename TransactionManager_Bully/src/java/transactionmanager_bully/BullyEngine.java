/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_bully;

import common.TransactionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 * Thread che verifica se il coordiantor è ancora attivo ed in caso contrario indice  una nuova elezione
 * @author marcx87
 */
public class BullyEngine implements Runnable{

    private volatile boolean running = false;
    private volatile boolean abort = false;

    private TopicSession session;
    private TopicPublisher publisher;
    private TopicSubscriber pingSubscriber;

    private TextMessage coordinatorMessage;
    private TextMessage pingMessage;

    private Election election;

    public BullyEngine(TopicConnection connection, Topic topic) {
        try {
            this.session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            publisher = session.createPublisher(topic);
            pingSubscriber = session.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.Pong + " AND " + MsgPropertyID.Destination + " = '" + TransactionManager.getClientID()+"'", true);
            coordinatorMessage = session.createTextMessage();
            coordinatorMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Coordinator);
            coordinatorMessage.setStringProperty(MsgPropertyID.CoorMessage, TransactionManager.getClientID());
            pingMessage = session.createTextMessage();
            pingMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Ping);
            pingMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            // Creating a new session for the Election thread (session is a single threaded object)
            election = new Election(connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE), topic);
        } catch (JMSException ex) {
            Logger.getLogger(BullyEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exit() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
                // Initiating startup election
                synchronized (this) {
                        System.out.println("Initiating startup election");
                        TransactionManager.setInhibitElection(true);
                        //startElection(publisher);
                        new Thread(election).start();
                }
                // Main cycle
                while(running) {
                        ping();
                        try {
                                // Timeout between pings
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {
                                running = false;
                        }
                }
                try {
                        session.close();
                } catch (JMSException ex) {
                       Logger.getLogger(BullyEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
    }

    private void ping() {
                synchronized(this) {
                        try {
                                // Send a test ping
                                if (!TransactionManager.getClientID().equals(TransactionManager.getCoordinator()) && !TransactionManager.getCoordinator().equals("0") && TransactionManager.getInhibitElection() == false) {
                                        //System.out.println("Sending a ping to the coordinator (client " + TransactionManager.getCoordinator() + ")");
                                        pingMessage.setStringProperty(MsgPropertyID.Destination, TransactionManager.getCoordinator());
                                        publisher.publish(pingMessage);
                                        TextMessage reply = null;
                                        reply = (TextMessage)pingSubscriber.receive(100);
                                        if (reply == null) {
                                                System.out.println("Ping failed! Starting an election");
                                                // Resetting clientID and starting a new election
                                                TransactionManager.setCoordinator("0");
                                                if (TransactionManager.getInhibitElection() == false) {
                                                        TransactionManager.setInhibitElection(true);
                                                        // startElection(publisher);
                                                        new Thread(election).start();
                                                }
                                        } else {
                                                //System.out.println("Ping succeeded! (" + reply.getText() + ")");
                                        }
                                }
                        } catch(JMSException ex) {
                                Logger.getLogger(BullyEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }

    public boolean isAbort() {
        return abort;
    }

    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    public Election getElection() {
        return election;
    }
}