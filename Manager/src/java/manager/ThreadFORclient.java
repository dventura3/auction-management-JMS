/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dto.Workflow;

/**
 * Thread che viene usato per avere una comunicazione dedicata tra Client e Server.
 * Infatti questa classe possiede e usa il socket per ricevere il WorkFlow da inserire nella coda e inviare una risposta (un int) al Client.
 * @author Daniela88, LuigiXIV, marcx87
 */
class ThreadFORclient implements Runnable
{
        /**
         * QueueManager.
         */
        private WFQueueManager qm;
        /**
         * Socket usato per comunicare con il Client.
         */
	private Socket s;
        /**
         * Stream di input.
         */
    	private ObjectInputStream inStream;
        /**
         * Stream di output.
         */
	private DataOutputStream outStream;
        /**
         * Variabile usata per contenere il risultato dell'inserimento in coda.
         * Se result == 1 --> inserimento avvenuto con successo.
         * Se result == -1 --> tentativo di inserire l'elemento in coda fallito!
         */
	private int result; //conserva il risultato dell'operazione di enqueu()

        /**
         * Costruttore.
         * @param qm QueueManager passatagli dal WFQueueThread.
         * @param s Socket passatagli dal WFQueueThread.
         */
	public ThreadFORclient(Socket s, WFQueueManager qManager){
            try {
                this.s = s;
                this.qm = qManager;
                inStream = new ObjectInputStream(s.getInputStream());
                outStream = new DataOutputStream(s.getOutputStream());

            } catch (IOException ex) {
                Logger.getLogger(ThreadFORclient.class.getName()).log(Level.SEVERE, null, ex);
            }
        result = -1;
	}

        /**
         * Implementazione del metodo run() contenuto nell'interfaccia Runnable.
         * Al suo interno viene ricevuto (tramite il socket) il WorkFlow da inserire in coda.
         * viene settato lo stream dentro il WFQueueManager per ricevere i file.
         * Viene invocato il metodo enqueue(wf).
         * Viene inviato il risultato dell'inserimento in coda al Client.
         * A questo punto il thread smette di esistere!
         */
    @Override
	public void run(){
		try{
                    //OPERAZIONI FATTE IN MODO ATOMICO --> è una sorta di transazione
                    synchronized(this)
                    {
                        qm.setCurrentStream(inStream);
			this.result = qm.enqueue((Workflow)inStream.readObject());
                    }
			outStream.writeInt(this.result);
                        outStream.close();
			s.close(); //è finito il lavoro del threadForClient... quindi chiudo il socket e al termine della run() si concluderà pure il thread.
		}
		catch(ClassNotFoundException ex){
			Logger.getLogger(ThreadFORclient.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch(IOException ex){
			Logger.getLogger(ThreadFORclient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
