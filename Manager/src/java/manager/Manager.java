/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import org.WFQueueInterface.AbstractDispatcher;
import org.dto.Activity;
import org.dto.Workflow;

/**
 * Thread manager = thread CONSUMATORE.
 * Al suo interno crea il thread WFQueueMananger e lo fa partire
 * @author Daniela88, LuigiXIV, marcx87
 */
public class  Manager implements Runnable, AbstractDispatcher{

        /**
         * ArrayList delle attivita'.
         */
        private ArrayList<Activity> activities;
        /**
         * Manager della coda di Workflow.
         */
	private WFQueueManager queueManager;
        /**
         * Thread che server per attivare il manager.
         */
        private Thread thread;
        /**
         * numero di porta a cui trovare il server
         */
        int numPorta;
        /**
         * Id della coda gestita dal server
         */
        private String IDQueue;

        private TopicConnection connectionFactory;
        private Topic topic2;

        /**
         * costruttore Manager. In esso viene inizializzato l'ArrayList delle attivita', la coda sotto forma di WFQueueManager e il thread.
         */
	public Manager(int numPorta, String IDQueue,TopicConnection connectionFactory,Topic topic2){
                
		this.activities = new ArrayList<Activity>();
		this.queueManager = new WFQueueManager(numPorta, IDQueue,connectionFactory,topic2);
                this.thread = new Thread(this);
                this.IDQueue = IDQueue;
                this.numPorta = numPorta;
                this.connectionFactory = connectionFactory;
                this.topic2 = topic2;
	}

        /**
         * Metodo per fare partire il thread manager
         */
        public void start(){
            thread.start();
        }

        /**
         * Implementazione del run dell'interfaccia Runnable.
         */
    @Override
	public void run(){
            new Thread(this.queueManager).start();
            while(true)
            {
                Workflow wf = this.queueManager.dequeue();
                System.out.println(wf);
                try {
                     Thread.sleep(4000);
                } catch (InterruptedException ex) {
                     Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
	}

        /**
         * Restituisce la lista di activity.
         * @return collezione di activity.
         */
        public Collection<Activity> getActivities()
        {
            return this.activities;
        }

        /**
         * Setta la lista di activity
         * @param val lista activity
         */
        public void setActivities(Collection<Activity> val)
        {
            this.activities = (ArrayList<Activity>) val;
        }

}
