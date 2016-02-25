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

public interface IPublisherLogic
{
   
   // Metodo di callback per notificare in maniera asincrona il cliente di eventuali problemi
   public void onException(JMSException ex);
   // Restituisce l'oggetto TopicConnection 
   public TopicConnection getConnection();
   // Restituisce una istanza della classe Publisher
   public Publisher getPublisher();
   // Restituisce il TextMessage del JMS message
   public TextMessage getMessage();
   
}
