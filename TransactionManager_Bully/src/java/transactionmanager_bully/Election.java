/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_bully;

import common.TransactionManager;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;
import org.dto.QueueItemDTO;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 * Thread ceh gestisce una nuova elezione
 * @author marcx87
 */
public class Election implements Runnable{
        private volatile boolean abort;

        private TopicPublisher publisher;

        private TextMessage electionMessage;
        private TextMessage coordinatorMessage;

    Election(TopicSession session, Topic topic) {
        super();
        try {
            electionMessage = session.createTextMessage();
            electionMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Election);
            electionMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
            coordinatorMessage = session.createTextMessage();
            coordinatorMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Coordinator);
            coordinatorMessage.setStringProperty(MsgPropertyID.CoorMessage, TransactionManager.getClientID());
            publisher = session.createPublisher(topic);
        } catch (JMSException ex) {
            Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAbort(boolean abort) {
            this.abort = abort;
    }

    public boolean isAbort() {
            return abort;
    }

    @Override
    public void run() {
        try {
                        // Send election request
                        setAbort(false);
                        publisher.publish(electionMessage);
                        // Wait for replies
                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                                Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (!isAbort()) {
                                // Nobody replied, I am the new coordinator
                                System.out.println("Election won!");
                                // Send new coordinator ID
                                publisher.publish(coordinatorMessage);
                                // Set coordinator
                                TransactionManager.setCoordinator(TransactionManager.getClientID());
                                // Remove election inhibition
                                TransactionManager.setInhibitElection(false);
                                TransactionManager.setUpdatedStatus(false);
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException ex) {
                                        Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if(TransactionManager.getUpdatedStatus() == false){
                                    TransactionManager.setUpdatedStatus(true);
                                    QueueManagerDAOImpl dao = new QueueManagerDAOImpl();
                                    LinkedList<QueueItemDTO> items = dao.getAllQueueData(TransactionManager.getClientID());
                                    for(QueueItemDTO item : items){
                                        TransactionManager.addQueueItem(item);
                                    }
                                }
                                else{
                                    TransactionManager.setUpdatedStatus(true);
                                }
                                synchronized(TransactionManager.sync){
                                    TransactionManager.sync.notifyAll();
                                }
                        } else {
                                System.out.println("Aborting election!");
                        }
                } catch (JAXBException ex) {
            Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
                        Logger.getLogger(Election.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
}