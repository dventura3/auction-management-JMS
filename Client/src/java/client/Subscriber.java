/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author Daniela
 */

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.*;
import org.dto.*;

public class Subscriber {

    Context jndi;
    TopicConnectionFactory connectionFactory;
    Topic topic;
    private TopicConnection connection;
    private TopicSession subSession;
    private TopicSubscriber subscriber;

    private String subName;
    private String messageSelector;
    private int IDWorkFlow;

    private Publisher p;
    private ListenerMSG listener;

    public Subscriber(String nome, Publisher p, int IDWorkFlow,TopicConnectionFactory connectionFactory,Topic topic){

        this.subName = nome;
        this.IDWorkFlow = IDWorkFlow;

        this.messageSelector = "CLIENT = '"+subName+"'" ;

        this.p = p;

        try {
//            //ottengo l'ogetto InitialContext
//            jndi = new InitialContext();
//            //trovo l'oggetto ConnectionFactory via JNDI
//            connectionFactory = (TopicConnectionFactory) jndi.lookup("jms/TopicConnectionFactory");
//            //Trova l’oggetto Destination via JNDI (Topic o Queue)
//            topic = (Topic) jndi.lookup("jms/Topic");
            this.connectionFactory = connectionFactory;
            this.topic = topic;
            //Richiede la creazione di un oggetto Connection all’oggetto ConnectionFactory
            connection = connectionFactory.createTopicConnection();
            // Crea un oggetto Session da Connection: primo parametro controlla transazionalità secondo specifica il tipo di ack
            subSession = connection.createTopicSession(false,Session.CLIENT_ACKNOWLEDGE);
            // Crea oggetto Subscriber da Session
            //subscriber = subSession.createSubscriber(topic);
            // Creo un DurableSubcriber così se il client CRASHA --> al suo ripristino gli vengono consegnati i messaggi
            // inoltre setto il messageSelector --> così riceve NON tutti i messaggi ma solo quelli a lui destinati
            subscriber = subSession.createSubscriber(topic, this.messageSelector , true);
            //creo il listener:
            this.listener = new ListenerMSG(this.subName,this.IDWorkFlow,this.p);
            // Registra MessageListener per l’oggetto TopicSubscriber desiderato
            subscriber.setMessageListener(this.listener);
            connection.start();
            System.out.println("SetupSubscriber --> connection.start()");
        }
        catch (JMSException e) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, e);
        }
//        catch (NamingException ex) {
//            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

  public void closeConnection(){
        try {
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  public boolean listAuctionBisIsEmpty(){
      System.out.println("In listAuctionBisIsEmpty del Subscriber.Java, con listener: " + this.listener.toString());
      if(this.listener.getListaAuctionBid().isEmpty()){
          return true;
      }
      else{
          return false;
      }
  }

  public AuctionBid getAuctionBidWinner(){
      return this.listener.calcolaBidWinner();
  }

  public LinkedList<AuctionBid> getlistaDiBidVincitori(){
      return this.listener.calcolaBidWinnerList();
  }

  public boolean BidWinnerIsEmpty(){
      if(this.listener.calcolaBidWinner()==null){
          return true;
      }
      return false;
  }

//  public int numAttualiVincitori(){
//      return this.listener.numAttualiVincitori();
//  }

  public int numAttualiVincitori(double offerta){
      return this.listener.numAttualiVincitoriPerOffertaPiuAlta(offerta);
  }
  
}
