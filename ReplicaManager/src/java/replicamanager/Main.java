/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package replicamanager;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.xml.bind.JAXBException;
import org.dao.QueueManagerDAOImpl;


/**
 *
 * @author luigi Sinatra, Daniela Ventura, Marco Messina
 */
public class Main {
    @Resource(mappedName = "jms/TopicConnectionFactory")
    private static TopicConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/Topic2")
    private static Topic topic;
    private static String managerId;
    private static Subscriber subscriber;
    private static org.dao.QueueManagerDAOImpl dao;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //if(args.length!=0){
        dao = new QueueManagerDAOImpl();
        if(args.length > 1)
        {
            System.out.println("Usage: java TransactionManager [NomeFileConfigurazione.xml]");
            System.exit(1);
        }
        else if(args.length == 1){
            try {
                managerId = dao.getReplicaId(args[0]);
                if(managerId == null){
                    System.out.println("File non trovato. Assegnamo un nuovo id");
                    managerId = UUID.randomUUID().toString();
                    dao.setReplicaId(managerId);
                }
            } catch (JAXBException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(args.length == 0){
            try {
                managerId = UUID.randomUUID().toString();
                dao.setReplicaId(managerId);
            } catch (JAXBException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        subscriber = new Subscriber(connectionFactory, topic,managerId);
        subscriber.run();
    }
}
