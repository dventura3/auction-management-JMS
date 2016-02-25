/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;
import org.dto.QueueItemDTO;
import org.dto.TMStatus;
import org.dto.VectorClock;
import org.dto.VectorComparison;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 * Message Listener implementation used to sense status messages
 * @author marcx87
 */
public class StatusMessageListener implements MessageListener{
    private TopicSession session;
    private TopicPublisher publisher;

    public StatusMessageListener(TopicConnection connection, Topic topic) {
        try {
            this.session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            this.publisher = session.createPublisher(topic);
        } catch (JMSException ex) {
            Logger.getLogger(StatusMessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        ObjectMessage msg = null;
        TMStatus status = null;
        QueueManagerDAOImpl dao = null;
        if(message instanceof ObjectMessage){
            msg = (ObjectMessage) message;
            try {
                //verifichiamo se il messaggio proviene da un TM di backup
                if(msg.propertyExists(MsgPropertyID.Destination) && msg.getStringProperty(MsgPropertyID.Destination).equals(TransactionManager.getClientID())){
                    //verifichiamo se il TM è nello stato di update
                    if(!TransactionManager.getUpdatedStatus()){
                        dao = new QueueManagerDAOImpl();
                        String[] Rmanagers = null;
                        status = (TMStatus) msg.getObject();
                        VectorComparison compare = VectorClock.compare(TransactionManager.getVectorClock(), status.getVectorClock());
                        //verifichiamo se il messaggio porta con se uno stato più aggiornato
                        if(compare.toString().equals("SMALLER")){
                            System.out.println("Status message arrived from backup");
                            TransactionManager.setVectorClock(status.getVectorClock());
                            if(status.getCurrentItemTransaction() != null && !status.getCurrentItemTransaction().getReplicaManager().isEmpty()){
                                status.getCurrentItemTransaction().getReplicaManager().toArray(Rmanagers);
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            } else if(status.getCurrentItemTransaction() != null){
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            }
                            for(QueueItemDTO item : status.getBufferedItems()){
                                dao.StoreSingleQueueData(item, TransactionManager.getClientID(), false);
                                TransactionManager.addQueueItem(item);
                                synchronized(TransactionManager.sync){
                                    TransactionManager.sync.notifyAll();
                                }
                            }
                        }
                    }
                }else{
                    //il messaggio proviene dal primary ed è indirizzato ai backup
                    //è un messaggio di tipo Add
                    if(msg.getJMSType().equals("Add")){
                        dao = new QueueManagerDAOImpl();
                        String[] Rmanagers = null;
                        status = (TMStatus) msg.getObject();
                        VectorComparison compare = VectorClock.compare(TransactionManager.getVectorClock(), status.getVectorClock());
                        //verifichiamo se è un vecchio messaggio
                        if(compare.toString().equals("SMALLER")){
                            System.out.println("Status message arrived from primary: Add");
                            if(status.getCurrentItemTransaction() != null && !status.getCurrentItemTransaction().getReplicaManager().isEmpty()){
                                status.getCurrentItemTransaction().getReplicaManager().toArray(Rmanagers);
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            } else if(status.getCurrentItemTransaction() != null){
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            }
                            for(QueueItemDTO item : status.getBufferedItems()){
                                dao.StoreSingleQueueData(item, TransactionManager.getClientID(), false);
                            }
                            TransactionManager.setVectorClock(status.getVectorClock());
                        }
                    } else if(msg.getJMSType().equals("Remove")){
                        //è un messaggio di tipo remove
                        dao = new QueueManagerDAOImpl();
                        String[] Rmanagers = null;
                        status = (TMStatus) msg.getObject();
                        VectorComparison compare = VectorClock.compare(TransactionManager.getVectorClock(), status.getVectorClock());
                         //verifichiamo se è un vecchio messaggio
                        if(compare.toString().equals("SMALLER")){
                            System.out.println("Status message arrived from primary: Remove");
                            if(status.getCurrentItemTransaction() != null && !status.getCurrentItemTransaction().getReplicaManager().isEmpty()){
                                status.getCurrentItemTransaction().getReplicaManager().toArray(Rmanagers);
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            } else if(status.getCurrentItemTransaction() != null){
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            }
                            for(QueueItemDTO item : status.getBufferedItems()){
                                dao.clearQueueItemData(item, TransactionManager.getClientID(), false);
                            }
                            TransactionManager.setVectorClock(status.getVectorClock());
                        }
                    } else if(msg.getJMSType().equals("Update")){
                        //è un messaggio di update
                        dao = new QueueManagerDAOImpl();
                        status = (TMStatus) msg.getObject();
                        VectorComparison compare = VectorClock.compare(TransactionManager.getVectorClock(), status.getVectorClock());
                         //verifichiamo se è un vecchio messaggio
                        if(compare.toString().equals("SMALLER")){
                            System.out.println("Status message arrived from primary: Update");
                            if(status.getCurrentItemTransaction() != null && !status.getCurrentItemTransaction().getReplicaManager().isEmpty()){
                                String[] Rmanagers = new String[status.getCurrentItemTransaction().getReplicaManager().size()];
                                status.getCurrentItemTransaction().getReplicaManager().toArray(Rmanagers);
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID(), Rmanagers);
                            } else if(status.getCurrentItemTransaction() != null){
                                dao.LogSingleQueueData(status.getCurrentItemTransaction(), status.getTwoPCState(), TransactionManager.getClientID());
                            }
                            TransactionManager.setVectorClock(status.getVectorClock());
                        }
                    }
                    //mandiamo l'ack al transaction manager coordinator
                    TextMessage ack = session.createTextMessage();
                    ack.setIntProperty(MsgPropertyID.Type, MessageTYPE.AckBackup);
                    ack.setStringProperty(MsgPropertyID.Destination, message.getStringProperty(MsgPropertyID.Source));
                    ack.setText(status.getVectorClock().toString());
                    publisher.publish(ack);
                }
            } catch (JMSException ex) {
                Logger.getLogger(StatusMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch(JAXBException ex){
                Logger.getLogger(StatusMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch(IOException ex){
                Logger.getLogger(StatusMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}