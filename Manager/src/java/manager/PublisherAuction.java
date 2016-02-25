/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

/**
 *
 * @author Daniela
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import org.dto.*;

public class PublisherAuction {

//    @Resource(name="jms/TopicConnectionFactory",mappedName = "jms/TopicConnectionFactory",type=javax.jms.TopicConnectionFactory.class)
//    private static TopicConnectionFactory connectionFactory;
//    @Resource(name="jms/Topic",mappedName = "jms/Topic",type=javax.jms.TopicConnectionFactory.class)
//    private static Topic topic;

 //   TopicConnectionFactory connectionFactory;
 //   Topic topic;
    TopicConnection connection;
    TopicSession pubSession;
    TopicPublisher publisher;
    private TextMessage textMessage;
    private ObjectMessage objMessage;
    private String pubIP;
    private String pubPort;

    PublisherAuction(String nome, String pubIP,String pubPort,TopicConnectionFactory connectionFactory,Topic topic){
        this.pubIP = pubIP;
        this.pubPort = pubPort;

        try {
            //Richiede la creazione di un oggetto Connection all’oggetto ConnectionFactory
            connection = connectionFactory.createTopicConnection();
            // Crea un oggetto Session da Connection: primo parametro controlla transazionalità secondo specifica il tipo di ack
            pubSession = connection.createTopicSession(false,Session.CLIENT_ACKNOWLEDGE);
            // Richiede la creazione di un oggetto MessageProducer all’oggetto Session
            //TopicPublisher per Pub/Sub
            // QueueSender per Point-to-Point
            publisher = pubSession.createPublisher(topic);
            // Avvia la Connection
            connection.start();
            // Creazione del text message:
            textMessage = (TextMessage) pubSession.createTextMessage();
            //Creazione del object message:
            objMessage = (ObjectMessage)pubSession.createObjectMessage();
        } catch (JMSException ex) {
            Logger.getLogger(PublisherAuction.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // fine costruttore

    public void sendBid(String nomeClient,AuctionTYPE typeAuction,int idWF,int numTD,double offerta,VectorClock vc){
        try {
            AuctionBid ab = new AuctionBid();
            ab.setAction(ActionTYPE.Bid);
            ab.setAuctionType(typeAuction);
            ab.setManagerId(MainManager.nomeManager);
            ab.setManagerIP(pubIP);
            ab.setManagerPort(pubPort);
            ab.setWorkflowId(idWF);
            ab.setNumTaskDescriptor(numTD);
            ab.setPriceOffered(offerta);
            ab.setVectorClock(vc);
            //SETTO LA PROPRIETà in modo che il msg arrivi solo al CLIENT che ha come identificativo "nomeClient"
            objMessage.setStringProperty("CLIENT",nomeClient);
            objMessage.setObject(ab);
            objMessage.setJMSType("Bid");
            publisher.publish(objMessage,DeliveryMode.PERSISTENT,4,500000); //5min
        } catch (JMSException ex) {
            Logger.getLogger(PublisherAuction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}