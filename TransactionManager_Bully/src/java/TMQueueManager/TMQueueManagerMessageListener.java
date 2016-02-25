/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TMQueueManager;

import common.TransactionManager;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;
import org.dto.QueueItemDTO;
import org.dto.TMStatus;
import org.dto.VectorClock;
import org.dto.VectorComparison;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;
import org.util.Pair;
import org.util.TwoPCState;

/**
 *
 * @author marcx87
 */
public class TMQueueManagerMessageListener implements MessageListener{
    private AckManager ackManager;
    private ObjectMessage statusMessage;

    public TMQueueManagerMessageListener(AckManager ackmanager, ObjectMessage om){
        this.ackManager = ackmanager;
        this.statusMessage = om;
    }
    @Override
    public void onMessage(Message message) {
        ObjectMessage msg = null;
        QueueItemDTO item = null;
        TMStatus status = null;
        QueueManagerDAOImpl dao = null;
        Pair<QueueItemDTO, TwoPCState> map;
         if(message instanceof ObjectMessage){
            if(TransactionManager.getClientID().equals(TransactionManager.getCoordinator()) && TransactionManager.getUpdatedStatus()){
            System.out.println("QueueManager message arrived");
            msg = (ObjectMessage) message;
                try {
                    item = (QueueItemDTO) msg.getObject();
                    if(VectorClock.compare(item.getVectorClock(), TransactionManager.getVectorClock()).toString().equals(VectorComparison.SMALLER.toString())){
                      System.out.println("E' arrivato un vecchio messaggio...lo scartiamo");
                    }else{
                        statusMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.Status);
                        statusMessage.setStringProperty(MsgPropertyID.Source, TransactionManager.getClientID());
                        TransactionManager.incrementVectorClock(item);
                        dao = new QueueManagerDAOImpl();
                        status = new TMStatus();
                        status.setVectorClock(TransactionManager.getVectorClock());
                        map = dao.getLogData(TransactionManager.getClientID());
                        if(map != null){
                            status.setCurrentItemTransaction((QueueItemDTO) map.getFirst());
                            status.setTwoPCState((TwoPCState) map.getSecond());
                        }
                        dao.StoreSingleQueueData(item, TransactionManager.getClientID(), false);
                        Thread sendAck = new Thread(ackManager);
                        status.setBufferedItems(dao.getAllQueueData(TransactionManager.getClientID()));
                        statusMessage.setObject(status);
                        ackManager.setBackupMessage(statusMessage, "Add");
                        ackManager.setArrivedMessage(msg);
                        sendAck.start();
                    }
                } catch (JAXBException ex) {
                    Logger.getLogger(TMQueueManagerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TMQueueManagerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(TMQueueManagerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JMSException ex) {
                    Logger.getLogger(TMQueueManagerMessageListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
