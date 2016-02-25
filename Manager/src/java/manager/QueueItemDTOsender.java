/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.xml.bind.JAXBException;
import org.dto.*;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 *
 * @author Daniela
 */
public class QueueItemDTOsender implements Callable{

    private TopicSession session;
    private TopicPublisher publisher;
    private TopicSubscriber ackSubscriber;
    private ObjectMessage objectToSend; //msg mandato
    private ObjectMessage objectArrived; //msg ricevuto
    private ResponseMessage response;

    public QueueItemDTOsender(TopicConnection connection, Topic topic){
         try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            publisher = session.createPublisher(topic);
            ackSubscriber = session.createSubscriber(topic, MsgPropertyID.Type + " = " + MessageTYPE.AckManager + " AND " + MsgPropertyID.Destination + " = '" + MainManager.nomeManager +"'", true);
            objectToSend = session.createObjectMessage();
            objectToSend.setIntProperty(MsgPropertyID.Type, MessageTYPE.QueueManager);
            objectToSend.setStringProperty(MsgPropertyID.Source, MainManager.nomeManager);
        } catch (JMSException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer call(){
        try {
            //mando il QueueItemDTO al TM
            publisher.publish(objectToSend);
            //mi metto in attesa di ricevere il risultato per 5sec.
            System.out.println("mi metto in attesa di ricevere il risultato per 5sec.");
            objectArrived = (ObjectMessage) ackSubscriber.receive(5000);
            System.out.println("objectArrived: " + objectArrived);
            if (objectArrived != null) {
                response = (ResponseMessage) objectArrived.getObject();
                //ricevuto il risultato devo aggiornare il vectorClock
                MainManager.incrementVectorClock(response.getVectorClock());
                //salvo il vc
                MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                return 1;
            }
            return 0;
        } catch (JAXBException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void setObjectToSend(Workflow wf, RequestTYPE rt){
        try {
            
            QueueItemDTO queueItem = new QueueItemDTO(MainManager.nomeManager, wf, new Date());
            //setto e salvo il VectroClock
            MainManager.incrementVectorClock(MainManager.vc);
            MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
            //costruisco il messaggio da mandare
            queueItem.setVectorClock(MainManager.vc);
            queueItem.setRequestType(rt);
            objectToSend.setObject(queueItem);
        } catch (JAXBException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(QueueItemDTOsender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
