/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.jms.*;
import org.WFQueueInterface.WFQueue;
import org.dto.RequestTYPE;
import org.dto.TaskDescriptor;
import org.dto.Workflow;

/**
 * Classe che implementa l'interfaccia WFQueue e gestisce la concorrenza tra i vari thread che tentano di inserire o estrarre Workflow al suo interno.
 * in più possiede il SERVERSOCKET -> ad ogni "accept" genera un ThreadFORclient.
 * @author Daniela88, LuigiXIV, marcx87
 */
public class WFQueueManager implements Runnable, WFQueue
{
        /**
         * Coda di dimensioni limitate in cui inserire ed estrarre i WorkFlow.
         */
        private static LinkedBlockingQueue<Workflow> wfQueue;
        /**
         * porta del server
         */
        protected int serverPort;
        /**
         * serverSocket
         */
        private ServerSocket serverSocket = null;
        /**
         * Stream corrente del thread che vuole invocare la queue(wf)
         */
        private ObjectInputStream currentStream  =null;
        /**
         * Id della coda wfQueue;
         */
        private String IDQueue;

        private TopicConnection connection;
        private Topic topic;

        /**
         * Costruttore principale.
         */
	public WFQueueManager(int numPorta, String IDQueue,TopicConnection connection,Topic topic){
                this.serverPort = numPorta;
                this.IDQueue = IDQueue;
		//wfQueue = new LinkedBlockingQueue<Workflow>();
                this.connection = connection;
                this.topic=topic;
	}
        /**
         * Implementazione del metoro Run() dell'interfaccia Runnable.
         * Al suo interno si usa il ServerSocket.
         */
    @Override
        public void run()
        {
                openServerSocket();
                while(true){
                     Socket clientSocket = null;
                     try {
                            clientSocket = this.serverSocket.accept();
                      } catch (IOException ex) {
                                throw new RuntimeException("Error accepting client connection", ex);
                      }
                     //ad ogni accept creo un nuovo ThreadFORclient.
                     ThreadFORclient ttt = new ThreadFORclient(clientSocket, this);
                     Thread tfc = new Thread(ttt);
                     tfc.start();
                }
        }
        /**
         * Implementazione del metodo enqueue dell'interfaccia WFQueue. E' utilizzato per inserire un WorkFlow (proveniente dal CLient) all'interno della coda.
         * Al suo interno SALVO i files nel FileSystem --> METODO PUSH.
         * @param wf Workflow da inserire in coda
         * @return int intero che rappresenta l'avvenuto inserimento (1) o l'errore (-1) nel tentativo di inserire un nuovo WorkFlow nella coda della lista.
         */
    @Override
	public int enqueue(Workflow wf){
                BufferedOutputStream bos = null;
                ArrayList<byte[]> files = new ArrayList<byte[]>();
                Properties prop = System.getProperties();
                //Directory corrente nella quale è in esecuzione il server.
                String currentDir = prop.getProperty("user.dir");
                try{
                         //Leggiamo i file associati al Workflow appena ricevuto
                    ObjectInputStream s = this.currentStream;
                    //i file vengono cosi' salvati: "directory del server + ID del task + nome originario del file"
                    //vengono inoltre aggiornati i link dei task così da poter essere recuperati in fasi successive.
                    files.addAll((ArrayList<byte[]>) s.readObject());
                    Iterator<byte[]> it = files.iterator();
                    ArrayList<TaskDescriptor> tasks = (ArrayList<TaskDescriptor>)wf.getTasks();
                    ListIterator<TaskDescriptor> tasksIterator = tasks.listIterator();
                    while(tasksIterator.hasNext())
                    {
                        TaskDescriptor tmp = tasksIterator.next();
                        int count = tmp.getInputs().size();
                        for(int i = 0; i < count; i++)
                        {
                            if(it.hasNext())
                            {
                                byte[] actualFile = it.next();
                                if(tmp.getInputs().get(i).contains(File.separator))
                                {
                                    String[] ext = tmp.getInputs().remove(i).split(Pattern.quote(File.separator));
                                    tmp.getInputs().add(i,currentDir + tmp.getID() + ext[ext.length-1]);
                                    bos = new BufferedOutputStream(new FileOutputStream(tmp.getID() + ext[ext.length-1]));
                                }
                                else
                                {
                                    String in = tmp.getInputs().remove(i);
                                    tmp.getInputs().add(i,currentDir+tmp.getID()+in);
                                    bos = new BufferedOutputStream(new FileOutputStream(tmp.getID() + in));
                                }
                                bos.write(actualFile);
                                bos.flush();
                                bos.close();
                            }
                        }
                    }
                    int result = MainManager.proxy.sendDTOtoReplica(wf, RequestTYPE.Enqueue);
                    if(result==1){
                        //inserisce il wf in coda. Mette in wait il theard se la coda è piena.
                        System.out.println("Metto in coda il workflow");
                        if(wfQueue == null)
                            wfQueue = new LinkedBlockingQueue<Workflow>();
                        wfQueue.put(wf);
                        return 1;
                    }
                    throw new Exception("TransactionMenager NON disponibile!");
		}
		catch(InterruptedException ex){  // if interrupted while waiting
			Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
			return -1;
		}
		catch(NullPointerException ex){
			Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
			return -2;
		} catch(IOException ex){
                        Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
			return -3;
                } catch(ClassNotFoundException ex){
                        Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
			return -4;
                } catch(Exception ex){
                        Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
			return -5;
                }
	}

        /**
         * Implementazione del metodo dequeue dell'interfaccia WFQueue. E' utilizzato per rimuovere un elemento (il Workflow che si trova in testa) dalla lista.
         * @return WorkFlow Rappresenta l'elemento rimosso.
         */
    @Override
	public Workflow dequeue(){
		Workflow tmp = null;
		try{
                    tmp = MainManager.queueTMP.take();
                    int result = MainManager.proxy.estractDTOfromQueue(tmp);
                    if(result==1){
                       
                        //restituisce e rimuove la testa della coda. Se la coda è vuota, aspetta che l'elemento sia disponibile
                        tmp = wfQueue.take();
                    }
                    else{
                      
                        MainManager.queueTMP.put(tmp);
                    }
		}
		catch(InterruptedException ex){  // if interrupted while waiting
			Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
			return tmp;
		}
		return tmp;
	}

        /**
         * Restituisce l'intera lista di Workflow.
         * @return BlockingQueue
         */
	public BlockingQueue<Workflow> getWfQueue(){
		return wfQueue;
	}

        /**
         * Setta la lista di BlockingQueue in base al parametro passatogli durante l'invocazione.
         * @param val Rappresenta la BlockingQueue da settare.
         */
	public void setWfQueue(BlockingQueue<Workflow> val){
		wfQueue = (LinkedBlockingQueue<Workflow>) val;
	}
        
        /**
         * Inizializzazione del ServerSocket.
         */
        private void openServerSocket() {
            try {
                this.serverSocket = new ServerSocket(this.serverPort);
            } catch (IOException ex) {
                throw new RuntimeException("Cannot open port " + this.serverPort, ex);
            }
        }

        /**
         * Setto il currentStream.
         * @param s stream associalto al socket per leggere i dati al suo interno (lo usiamo per leggere i files).
         */
        public void setCurrentStream(ObjectInputStream s){
            this.currentStream = s;
        }

        public synchronized static Workflow returnWF(int idWF){
            if(!wfQueue.isEmpty()){
                Iterator<Workflow> it =  wfQueue.iterator();
                while(it.hasNext()){
                    Workflow wf = (Workflow)it.next();
                    if(wf.getID()==idWF){
                        return wf;
                    }
                }
            }
            return null;
        }

        public static void setWFQueueFromStatus(Workflow wf){
            try {
                if(wfQueue == null)
                    wfQueue = new LinkedBlockingQueue<Workflow>();
                wfQueue.put(wf);
            } catch (InterruptedException ex) {
                Logger.getLogger(WFQueueManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

}
