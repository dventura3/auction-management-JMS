/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package replicamanager;

/**
 *
 * @author  Luigi Sinatra, Marco Messina, Daniela Ventura
 */

// JMS imports
import javax.jms.*;
public interface ISubscriber {
    // Metodo di callback per gestire i messaggi in arrivo nel topic
    public void onMessage(Message message);
    // Metodo di callback per notificare in maniera asincrona il client di eventuali problemi
    public void onException(JMSException ex);
    
    // Restituisce un oggetto di tipo TopicConnection
    public TopicConnection getTopicConnection();
    // Restituisce un oggetto di tipo TopicSession
    public TopicSession getSession();
    // Restituisce il durable subscriber
    public TopicSubscriber getSubscriber();
    // Metodo per verificare se un subscriber è connesso o meno ad un topic
    public boolean isConnected();
   

}
