/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_bully;

import common.TransactionManager;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TopicPublisher;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;
import org.dto.QueueItemDTO;
import org.dto.TMStatus;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;
import org.util.Pair;
import org.util.TwoPCState;
/**
 * MessageListener implementation used to receive the new coordinator message
 * @author marcx87
 */
public class CoordinatorMessageListener implements MessageListener {
    private BullyEngine bullyEngine;

    private TopicPublisher currentStatusPublisher;
    
    private ObjectMessage statusMessage;

    public CoordinatorMessageListener(BullyEngine bullyEngine, TopicPublisher currentStatusPublisher, ObjectMessage statusMessage){
        this.bullyEngine = bullyEngine;
        this.currentStatusPublisher = currentStatusPublisher;
        this.statusMessage = statusMessage;
    }

    @Override
    public void onMessage(Message message) {
            synchronized (bullyEngine) {
                    String newCoordinator;
                    try {
                            newCoordinator = message.getStringProperty(MsgPropertyID.CoorMessage);
                            // Set coordinator only if its coordinatorID is greater than the clientID,
                            // otherwise start a new election
                            if (newCoordinator.compareTo(TransactionManager.getClientID())>0) {
                                    // Set the new coordinator
                                    QueueManagerDAOImpl dao = new QueueManagerDAOImpl();
                                    Pair<QueueItemDTO, TwoPCState> map;
                                    try {
                                        map = dao.getLogData(TransactionManager.getClientID());
                                        TransactionManager.setCoordinator(newCoordinator);
                                        // Remove the inhibition. From now on this client
                                        // may start a new election
                                        TransactionManager.setInhibitElection(false);
                                        System.out.println("New coordinator is Client " + newCoordinator);
                                        statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
                                        statusMessage.setStringProperty(MsgPropertyID.Destination, TransactionManager.getCoordinator());
                                        TMStatus status = new TMStatus();
                                        status.setVectorClock(TransactionManager.getVectorClock());
                                        if(map != null){
                                            status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                                            status.setTwoPCState((TwoPCState) map.getSecond());
                                        }
                                        status.setBufferedItems(dao.getAllQueueData(TransactionManager.getClientID()));
                                        statusMessage.setObject(status);
                                        currentStatusPublisher.publish(statusMessage);
                                    } catch (JAXBException ex) {
                                        Logger.getLogger(CoordinatorMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IOException ex) {
                                        Logger.getLogger(CoordinatorMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (ParseException ex) {
                                        Logger.getLogger(CoordinatorMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            } else {
                                    System.out.println("Setting Coordinator to 0!!!!!");
                                    TransactionManager.setCoordinator("0");
                            }
                    } catch (NumberFormatException ex) {
                           Logger.getLogger(CoordinatorMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JMSException ex) {
                           Logger.getLogger(CoordinatorMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
    }
 }