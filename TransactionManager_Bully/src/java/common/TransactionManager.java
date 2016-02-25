/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;
import org.dto.QueueItemDTO;
import org.dto.VectorClock;

/**
 *
 * @author marcx87
 */
public class TransactionManager {

    private static String clientID;
    private static String coordinator = "1";
    private static String transactionManagerId;
    private static boolean inhibitElection = false;
    private static boolean updatedStatus = false;
    private static LinkedList<QueueItemDTO> TMQueueItem;
    private static VectorClock vectorClock = new VectorClock();
    public static final Object sync = new Object();
    @Resource(mappedName = "jms/TopicConnectionFactory")
    private static TopicConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/Topic2")
    private static Topic topic;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            TMQueueItem = new LinkedList<QueueItemDTO>();
            QueueManagerDAOImpl dao = new QueueManagerDAOImpl();
            if(args.length > 1){
                System.out.println("Usage: java TransactionManager [NomeFileConfigurazione.xml]");
                System.exit(1);
            } else if(args.length == 1){
                clientID = dao.getTransactionManagerId(args[0]);
                if (clientID == null) {
                    System.out.println("File non trovato. Assegnamo un nuovo id");
                    clientID = UUID.randomUUID().toString();
                    dao.setTransactionManagerId(clientID);
                } else{
                   TMQueueItem = dao.getAllQueueData(clientID);
                }
            } else if(args.length == 0){
                clientID = UUID.randomUUID().toString();
                dao.setTransactionManagerId(clientID);
            }
            String TransactionManagerId = "TransactionManagerID" ;
            byte[] output;
           
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(TransactionManagerId.getBytes());
            output = md.digest();
            transactionManagerId = bytesToHex(output);
            vectorClock.incrementClock(transactionManagerId);
            Comunication comunication = new Comunication(connectionFactory, topic);
            comunication.start();
            System.out.println("System started");
            System.out.println("Press enter to stop...");

            System.in.read();
            comunication.stop();
            System.out.println("Bully client terminated");
         } catch (JAXBException ex) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch(ParseException ex){
            Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static synchronized String getClientID() {
                return clientID;
        }

        public static synchronized void setCoordinator(String newCoordinator) {
                coordinator = newCoordinator;
        }

        public static synchronized VectorClock getVectorClock(){
            return vectorClock;
        }

        public static synchronized void incrementVectorClock(QueueItemDTO item){
            if(item.getVectorClock() != null){
            vectorClock = VectorClock.max(vectorClock, item.getVectorClock());
            }
            vectorClock.incrementClock(transactionManagerId);
        }

        public static synchronized void setVectorClock(VectorClock cl){
            vectorClock = cl;
        }

        public static String getCoordinator() {
                return coordinator;
        }

        public static boolean getInhibitElection() {
                return inhibitElection;
        }

        public static void setInhibitElection(boolean newInhibitElection) {
                inhibitElection = newInhibitElection;
        }

        public static synchronized boolean getUpdatedStatus(){
            return updatedStatus;
        }

        public static synchronized void setUpdatedStatus(boolean newUpdatedStatus){
            updatedStatus = newUpdatedStatus;
        }

        public static synchronized QueueItemDTO getFirstItem(){
           try{
               QueueItemDTO tmp = TMQueueItem.getFirst();
               return tmp;
           }
           catch(NoSuchElementException e){
               return null;
           }
       }

        public static synchronized void addQueueItem(QueueItemDTO item){
            TMQueueItem.add(item);
            Collections.sort(TMQueueItem);
        }

        public static synchronized void removeFirstItem(){
            try {
                QueueItemDTO item = TMQueueItem.removeFirst();
                QueueManagerDAOImpl dao = new QueueManagerDAOImpl();
                dao.clearQueueItemData(item, clientID, false);
            } catch (JAXBException ex) {
                Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TransactionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public static String bytesToHex(byte[] b) {
            char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                                         '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            StringBuilder buf = new StringBuilder();
            for (int j=0; j<b.length; j++) {
                buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
                buf.append(hexDigit[b[j] & 0x0f]);
            }
            return buf.toString();
        }
}
