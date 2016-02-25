/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import org.dto.*;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 *
 * @author Daniela
 */
public class TMproxy {

    TopicConnection connection;
    Topic topic2; // topic tra Manager e TM
    Topic topic; // topic tra Manger e Client
    QueueItemDTOsender threadQueueItem;

    public TMproxy(TopicConnection connection, Topic topic2, Topic topic){
        try {
            this.connection = connection;
            this.topic2 = topic2;
            this.topic = topic;
            this.connection.start();
            threadQueueItem = new QueueItemDTOsender(connection, topic2);
        } catch (JMSException ex) {
            Logger.getLogger(TMproxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int sendDTOtoReplica(Workflow wf, RequestTYPE rt){

        int val = 0;
        int i=0;

        while(i<3 && val==0){
            try {
                threadQueueItem.setObjectToSend(wf, rt);
                val = threadQueueItem.call();
                if (val == 1) {
                    System.out.println("Il QueueItemDTO è stato mandato al TM con successo!");
                    return val;
                } else {
                    System.out.println("Il QueueItemDTO NON è stato mandato correttamente!");
                }
            } catch (Exception ex) {
                Logger.getLogger(TMproxy.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
        return val;
    }

    public int estractDTOfromQueue(Workflow wf){
        int val = sendDTOtoReplica(wf,RequestTYPE.Dequeue);
        if(val==1){
            try {
                DequeueListener dequeue = new DequeueListener(connection, topic2);
                int result = dequeue.call();
                return result;
            } catch (Exception ex) {
                Logger.getLogger(TMproxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public void createSubscriberTM(){
        try {
            TopicSession subSession = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);
            TopicSubscriber subscriber = subSession.createSubscriber(topic2, MsgPropertyID.Type + " = " + MessageTYPE.TransactionEnqueue + " AND " + MsgPropertyID.Destination +  " = '" + MainManager.nomeManager +"'",true);
            TopicSubscriber sub = subSession.createSubscriber(topic,MsgPropertyID.Type + " = " + MessageTYPE.IsManagerAlive + " AND " + MsgPropertyID.Destination +  " = '" + MainManager.nomeManager +"'",true);
            subscriber.setMessageListener(new SubscriberTM(connection,topic));
            sub.setMessageListener(new ManagerIsAliveMessageListener(connection,topic));
        } catch (JMSException ex) {
            Logger.getLogger(TMproxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int downloadStatusQueue(){
        //all'andata mando un DTO con WF null e RequestTYPE.QueueStatus --> pubisher su "topic2"
        //al ritorno ricevo un ArrayList di WF da inserire in coda! --> subscriber che aspetta per un pò su "topic2"
        //notifica ai client con "Ack" --> publisher verso il "topic" del Client
        DownloaderStatusQueue threadDownload = new DownloaderStatusQueue(connection, topic2,topic);
        int val = threadDownload.call();
        return val;
    }
}
