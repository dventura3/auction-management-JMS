/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author Daniela
 */

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.*;
import javax.xml.bind.JAXBException;
import org.dto.*;

public class Publisher {
    
//    Context jndi;
    TopicConnectionFactory connectionFactory;
    Topic topic;
    TopicConnection connection;
    TopicSession pubSession;
    TopicPublisher publisher;
    private String pubName;
    private ObjectMessage objMessage;
    private TextMessage textMessage;

    Publisher(String nome,TopicConnectionFactory connectionFactory,Topic topic){
        pubName = nome;

        try {
//            ottengo l'ogetto InitialContext
//            jndi = new InitialContext();
//            trovo l'oggetto ConnectionFactory via JNDI
//            connectionFactory = (TopicConnectionFactory) jndi.lookup("jms/TopicConnectionFactory");
//            Trova l’oggetto Destination via JNDI (Topic o Queue)
//            topic = (Topic) jndi.lookup("jms/Topic");
            this.connectionFactory = connectionFactory;
            this.topic = topic;
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
            //Creazione del object message:
            objMessage = (ObjectMessage)pubSession.createObjectMessage();
            //Creazione del text message:
            textMessage = (TextMessage)pubSession.createTextMessage();
            System.out.println("Publisher costruttore() --> creato connessioni e sessioni");
        } catch (JMSException e) {
            System.out.println("Exception occurred: " + e.toString());
        }
//        catch(NamingException e){
//            System.out.println("Exception occurred: " + e.toString());
//        }
    } // fine costruttore

    public void sendStatus(AuctionTYPE typeAuction,int idWF,int numTD,double offerta,String attualeWinner){
        try {
            //sto mandando un messaggio--> allora incremento il VectorClock;
            MiddlewareMain.incrementVectorClock(MiddlewareMain.vc);
            //salvo il vectorClock nel file di LOG
            MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
            //creo l'AcutionStatus da mandare
            AuctionStatus as = new AuctionStatus();
            as.setAction(ActionTYPE.Status);
            as.setTypeAuction(typeAuction);
            as.setClientId(pubName);
            as.setWorkflowId(idWF);
            as.setNumTaskDescriptor(numTD);
            as.setCurrentPrice(offerta);
            as.setCurrentWinner(attualeWinner);
            as.setVectorClock(MiddlewareMain.vc);
            objMessage.setObject(as);
            objMessage.setJMSType("Status");
            publisher.publish(objMessage);
            System.out.println("SendStatus: MESSAGGIO INVIATO!");
        } catch (JAXBException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendEndAuction(AuctionTYPE typeAuction,int idWF,int numTD,double offerta,String attualeWinner){
        try {

            //sto mandando un messaggio--> allora incremento il VectorClock;
            MiddlewareMain.incrementVectorClock(MiddlewareMain.vc);
            //salvo il vectorClock nel file di LOG
            MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
            //creo l'AcutionStatus da mandare
            AuctionStatus as = new AuctionStatus();
            as.setAction(ActionTYPE.EndAuction);
            as.setTypeAuction(typeAuction);
            as.setClientId(pubName);
            as.setWorkflowId(idWF);
            as.setNumTaskDescriptor(numTD);
            as.setCurrentPrice(offerta);
            as.setCurrentWinner(attualeWinner);
            as.setVectorClock(MiddlewareMain.vc);
            objMessage.setObject(as);
            objMessage.setJMSType("EndAuction");
            publisher.publish(objMessage);
            System.out.println("sendEndAuction: MESSAGGIO di FINE ASTA INVIATO!");
        } catch (JAXBException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
