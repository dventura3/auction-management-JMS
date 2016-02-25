/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.WFQueueInterface.WFQueue;

/**
 * Classe che implementa il tipo WFQueue e gestisce le chiamate remote.
 * @author Daniela88, LuigiXIV, marcx87
 * @see WFQueue
 */
public class WFQueueManagerProxy implements WFQueue
{
    /**
     * Nome dell'host in cui risiede il server.
     */
    private static String server_hostname = "localhost";
    /**
     * Porta in cui è in ascolto il server.
     */
    private static int port= 9190;

    /**
     * Metodo che permette di inserire in coda un Workflow.
     * @param wf Workflow da inserire in coda.
     * @return Indice della coda nel quale è inserito il workflow o -1 se vi sono stati errori durante l'inserimento.
     */
    @Override
    public int enqueue(org.dto.Workflow wf)
    {
        ArrayList <byte[]> filesToSend = new ArrayList<byte[]>();
        Iterator it = wf.getTasks().iterator();
        Socket clientSocket = null;
        DataInputStream in = null;
        ObjectOutputStream out = null;
        BufferedInputStream bis = null;
        byte[] data;
        int result = -1;
        try {
            while(it.hasNext())
            {
                org.dto.TaskDescriptor element = (org.dto.TaskDescriptor)it.next();
                Iterator<String> ite = element.getInputs().iterator();
                while(ite.hasNext())
                {
                    String file =ite.next();
                    bis = new BufferedInputStream(new FileInputStream(file));
                    data = new byte[bis.available()];
                    bis.read(data);
                    filesToSend.add(data);
                    bis.close();
                }
            }
            clientSocket  = new Socket(InetAddress.getByName(server_hostname), port);
            in = new DataInputStream(clientSocket.getInputStream());
            out  =new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(wf);
            out.writeObject(filesToSend);
            result = in.readInt();
            out.close();
            in.close();
        } catch(ConnectException ex){
            Logger.getLogger(WFQueueManagerProxy.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (UnknownHostException ex) {
            Logger.getLogger(WFQueueManagerProxy.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } catch(FileNotFoundException ex) {
            Logger.getLogger(WFQueueManagerProxy.class.getName()).log(Level.SEVERE, null, ex);
            return -3;
        }
        catch(IOException ex) {
            Logger.getLogger(WFQueueManagerProxy.class.getName()).log(Level.SEVERE, null, ex);
            return -4;
        }
        finally
        {
            try {
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(WFQueueManagerProxy.class.getName()).log(Level.SEVERE, null, ex);
                return -5;
            } catch(NullPointerException ex){
                Logger.getLogger(WFQueueManagerProxy.class.getName()).log(Level.SEVERE, null, ex);
                return -6;
            }
        }
        return result;
    }

    /**
     * Metodo per l'estrazione di un Workflow dalla coda (NON UTILIZZATO).
     * @return Il workflow estratto o null se vi sono stati errori.
     */
    @Override
    public org.dto.Workflow dequeue()
    {
          return null;
    }

    public String getServerHostname()
    {
        return server_hostname;
    }

    public void setServerHostname(String hostName)
    {
        server_hostname = hostName;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int pt)
    {
        port = pt;
    }
}

