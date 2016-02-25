/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.xml.bind.JAXBException;
import org.dto.ResponseMessage;
import org.util.Pair;

/**
 *
 * @author Daniela
 */
public class ListenerAckWorkflow implements MessageListener{

    private ObjectMessage objMessage;
    private ResponseMessage response;
    public static Pair<Integer,String> result;

    public ListenerAckWorkflow(){
        result = new Pair(0, null);
    }

    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            try {
                objMessage = (ObjectMessage) message;
                String type = objMessage.getJMSType();
                System.out.println("ListenerAckWorkflow(): E' arrivato un messaggio di tipo: "+type);
                response = (ResponseMessage)objMessage.getObject();
                synchronized(MiddlewareMain.sync){
                    while(result.getSecond()!=null){
                        try {
                            System.out.println("Sono Bloccato LAW");
                            MiddlewareMain.sync.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ListenerAckWorkflow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //il WorfkFlow è stato inserito in coda correttamente
                    if (type.equals("Ack")) {
                        //aggiorno il VectroClock
                        MiddlewareMain.incrementVectorClock(response.getVectorClock());
                        //salvo il vectorClock nel file di LOG
                        MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
                        String IDwf = response.getMessage();
                        if(AsWorkflowResponce.containsWorkflow(Integer.parseInt(IDwf))){
                            //se l'ID del WF contenuto dentro il messaggio è uguale a quello che sto considerando
                            //altrimenti lo scarto
                            System.out.println("Ack!");
                            result= new Pair<Integer,String>(Integer.parseInt(IDwf),"Ack");
                        }
                    }
                    if (type.equals("Nack")) {
                        //aggiorno il VectroClock
                        MiddlewareMain.incrementVectorClock(response.getVectorClock());
                        //salvo il vectorClock nel file di LOG
                        MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
                        String IDwf = response.getMessage();
                        if(AsWorkflowResponce.containsWorkflow(Integer.parseInt(IDwf))){
                            //se l'ID del WF contenuto dentro il messaggio è uguale a quello che sto considerando
                            //altrimenti lo scarto
                            System.out.println("Nack!");
                            result= new Pair<Integer,String>(Integer.parseInt(IDwf),"Nack");
                        }
                    }
                    if (type.equals("Wait")) {
                        //aggiorno il VectroClock
                        MiddlewareMain.incrementVectorClock(response.getVectorClock());
                        //salvo il vectorClock nel file di LOG
                        MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
                        String IDwf = response.getMessage();
                        if(AsWorkflowResponce.containsWorkflow(Integer.parseInt(IDwf))){
                            //se l'ID del WF contenuto dentro il messaggio è uguale a quello che sto considerando
                            //altrimenti lo scarto
                            System.out.println("Wait!");
                            result= new Pair<Integer,String>(Integer.parseInt(IDwf),"Wait");
                        }
                    }
                    MiddlewareMain.sync.notifyAll();
                }

            } catch (JAXBException ex) {
                Logger.getLogger(ListenerAckWorkflow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ListenerAckWorkflow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMSException ex) {
                Logger.getLogger(ListenerAckWorkflow.class.getName()).log(Level.SEVERE, null, ex);
            } catch(Exception ex){
                Logger.getLogger(ListenerAckWorkflow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
