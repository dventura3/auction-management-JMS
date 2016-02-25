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

public class Subscriber {


//    @Resource(name="jms/TopicConnectionFactory",mappedName = "jms/TopicConnectionFactory",type=javax.jms.TopicConnectionFactory.class)
//    private static TopicConnectionFactory connectionFactory;
//    @Resource(name="jms/Topic",mappedName = "jms/Topic",type=javax.jms.TopicConnectionFactory.class)
//    private static Topic topic;

    //TopicConnectionFactory connectionFactory;
    //Topic topic;
    private TopicConnection connection;
    private TopicSession subSession;
    private TopicSubscriber subscriber;

    private String subName;
    private String subIP;
    private String subPort;

    PublisherAuction p; // il manager è subscriber degli "status" ma publisher dei "bid"
    ListenerMSG listener;

    public Subscriber(String nome,String ip, String port,TopicConnectionFactory connectionFactory,Topic topic){
        try {
            subName = nome;
            subIP = ip;
            subPort = port;
            p = new PublisherAuction(this.subName, this.subIP, this.subPort, connectionFactory, topic);
            connection = connectionFactory.createTopicConnection();
            // Crea un oggetto Session da Connection: primo parametro controlla transazionalità secondo specifica il tipo di ack
            subSession = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);
            // Crea oggetto Subscriber da Session
            subscriber = subSession.createSubscriber(topic);
            //creo il listener
            listener = new ListenerMSG(this.subName, this.subIP, this.subPort, this.p);
            // Registra MessageListener per l’oggetto TopicSubscriber desiderato
            subscriber.setMessageListener(listener);
            connection.start();
        } catch (JMSException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

}
