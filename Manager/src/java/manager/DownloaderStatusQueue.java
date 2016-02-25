/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBException;
import org.dto.*;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 *
 * @author Daniela
 */
public class DownloaderStatusQueue implements Callable{

    // Riguardante topic2
    private TopicSession session;
    private TopicPublisher publisher_topic2;
    private TopicSubscriber ackSubscriber;
    private TopicSubscriber statusQueueSubscriber;
    private ObjectMessage objectToSend; //msg mandato
    private ObjectMessage objectArrived; //msg ricevuto
    private ObjectMessage objectArrived2; //msg ricevuto

    // Riguardante topic
    private TopicPublisher publisher_topic;
    private ObjectMessage objMessage;

    public DownloaderStatusQueue(TopicConnection connection, Topic topic2, Topic topic){
        try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //topic2 - ACK:
            publisher_topic2 = session.createPublisher(topic2);
            ackSubscriber = session.createSubscriber(topic2, MsgPropertyID.Type + " = " + MessageTYPE.AckManager + " AND " + MsgPropertyID.Destination + " = '" + MainManager.nomeManager +"'", true);
            objectToSend = session.createObjectMessage();
            objectToSend.setIntProperty(MsgPropertyID.Type, MessageTYPE.QueueManager);
            objectToSend.setStringProperty(MsgPropertyID.Source, MainManager.nomeManager);
            //topic2 - Lista DTO:
            statusQueueSubscriber = session.createSubscriber(topic2, MsgPropertyID.Type + " = " + MessageTYPE.QueueStatus + " AND " + MsgPropertyID.Destination + " = '" + MainManager.nomeManager +"'", true);
            //topic - per inviare l'ack al Client:
            publisher_topic = session.createPublisher(topic);
            objMessage = session.createObjectMessage();
        } catch (JMSException ex) {
            Logger.getLogger(DownloaderStatusQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer call(){
        try {
            //setto il DTO da manadare con WF=null e RequestTYPE.QueueStatus
            QueueItemDTO queueItem = new QueueItemDTO(MainManager.nomeManager, null, new Date());
            //setto e salvo il VectroClock
            MainManager.incrementVectorClock(MainManager.vc);
            MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
            //setto il messaggio da mandare
            queueItem.setRequestType(RequestTYPE.QueueStatus);
            queueItem.setVectorClock(MainManager.vc);
            objectToSend.setObject(queueItem);
            //mando il QueueItemDTO al TM
            publisher_topic2.publish(objectToSend);
            
                       //mi metto in attesa di ricevere l'ack da parte del TM
            System.out.println("Mi metto in attesa di ricevere l'ack da parte del TM");
            int i=1;
            boolean objectIsArrived = false;
            while(i<=3 && objectIsArrived==false){
                System.out.println("Prova numero: "+i);
                i=i+1;
                objectArrived = (ObjectMessage)ackSubscriber.receive(5000);//5sec
                if(objectArrived!=null){
                    objectIsArrived = true;

                    ResponseMessage responseACK = new ResponseMessage();
                    responseACK = (ResponseMessage)objectArrived.getObject();
                    //ricevuto il risultato devo aggiornare il vectorClock e salvare su file
                    MainManager.incrementVectorClock(responseACK.getVectorClock());
                    MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                    System.out.println("Ack da parte del TM ricevuto! Adesso aspetto di ricevere lo stato della Queue");

                    //aspetto che mi arrivi la coda vera e propria di DTO
                    objectArrived2 = (ObjectMessage)statusQueueSubscriber.receive(30000);//30sec
                    if(objectArrived2!=null){
                        ManagerStatus listWFtoInsertInQueue = null;
                        listWFtoInsertInQueue = (ManagerStatus)objectArrived2.getObject();
                        //ricevuto il msg devo aggiornare il vectroClock e salvare il VC
                        MainManager.incrementVectorClock(listWFtoInsertInQueue.getVectorClock());
                        MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                        System.out.println("Stato della Queue ricevuto! Adesso inserisco i Workflow (se ci sono!) in coda");

                        if(listWFtoInsertInQueue!=null){
                            if(listWFtoInsertInQueue.getItems()!=null){
                                Iterator<QueueItemDTO> iteratorListDTO = listWFtoInsertInQueue.getItems().iterator();
                                while(iteratorListDTO.hasNext()){
                                    Workflow wf = iteratorListDTO.next().getWorkflow();

                                    //inserisco un WF alla volta dentro la coda sincronizzata
                                    WFQueueManager.setWFQueueFromStatus(wf);
                                    System.out.println("Inserisco dentro la coda SINCRONIZZATA il wf: "+wf.getID());

                                    //Per ogni singolo WF ricevuto, mando le notifiche ai Client (su "topic"):
                                    ResponseMessage r = new ResponseMessage();
                                    r.setMessage(String.valueOf(wf.getID()));
                                    MainManager.queueTMP.add(wf); //inserisco ne buffer temporaneo per gli elementi su cui poter effettuare DEQUEUE
                                    objMessage.setJMSType("Ack");
                                    objMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.WorkflowResponse);
                                    MainManager.incrementVectorClock(MainManager.vc);
                                    //salvo il vectorClock nel file di LOG
                                    MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                                    r.setVectorClock(MainManager.vc);
                                    objMessage.setObject(r);
                                    publisher_topic.publish(objMessage);
                                    System.out.println("Mando l'Ack per notificare al Client l'enqueue avvenuta con successo!");
                                }
                                return 1;
                            }
                           return 0;
                        }
                        return 0;
                    }
                    return 0;
                }
            }//fine while
        } catch (JAXBException ex) {
            Logger.getLogger(DownloaderStatusQueue.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } catch (IOException ex) {
            Logger.getLogger(DownloaderStatusQueue.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } catch (JMSException ex) {
            Logger.getLogger(DownloaderStatusQueue.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return 0;
    }

}
