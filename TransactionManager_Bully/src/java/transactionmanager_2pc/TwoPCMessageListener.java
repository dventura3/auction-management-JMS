/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_2pc;

/**
 *
 * @author Daniela Luigi MArco
 */
import java.util.ArrayList;
import javax.jms.*;
import java.util.Iterator;
import org.dto.QueueItemDTO;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

public class TwoPCMessageListener implements MessageListener,ExceptionListener {
    private boolean isReceivedAllReady;
    private boolean isReceivedAllAck;
    private int counterAck = 0;
    private TwoPCEngine engine;
    
    public TwoPCMessageListener(TwoPCEngine engine, TopicSession session){
        this.engine = engine;
        isReceivedAllReady = false;
    }

    @Override
    //Meccanismo di callback: agisce durante la ricezione di un messaggios
    public void onMessage(Message message){
        try {
        //Verifica che sia un messaggio di testo
            if (message instanceof TextMessage)
            {
                TextMessage rcvdMessage = (TextMessage) message;
                String text = rcvdMessage.getText();
                String type = rcvdMessage.getJMSType();
                System.out.println("Messaggio arrivato: " + text);
                if(type.equals("Join"))
                {
                    System.out.println("Arrivato Join da: " + text);
                    isReceivedAllReady = false;
                    isReceivedAllAck = false;
                    counterAck = 0;
                    this.engine.getReplicaManagers().put(text,null);
                }
                else if(type.equals("Ready") && !isReceivedAllReady && !this.engine.isTimeToWaitReadyMessagesElapsed())
                {
                    if(this.engine.getReplicaManagers().containsKey(text.split("#")[0]))
                    {
                        Iterator<String> it = this.engine.getReplicaManagers().keySet().iterator();
                        while(it.hasNext())
                        {
                            String tmp = it.next();
                            if(tmp.equals(text.split("#")[0]))
                            {
                                this.engine.getReplicaManagers().put(tmp, Integer.parseInt(text.split("#")[1]));
                                break;
                            }
                        }
                        checkIfReceiveAllReady();
                    }
                }
                else if(type.equals("Ack") && isReceivedAllAck == false && this.engine.isTimeToReceiveAckElapsed() == false)
                {
                    System.out.println("Ricevuto Ack da: " + text);
                    if(this.engine.getReplicaManagers().containsKey(text))
                    {
                        counterAck++;
                        if(counterAck == this.engine.getReplicaManagers().size())
                        {
                            isReceivedAllAck = true;
                            this.engine.completeTransaction();
                        }
                        else
                        {
                            isReceivedAllAck = false;
                        }
                    }
                }
            }
            else if (message instanceof ObjectMessage)
            {
                ObjectMessage receivedMessage = (ObjectMessage) message;
                if(receivedMessage.getIntProperty(MsgPropertyID.Type) == MessageTYPE.QueueStatus)
                {
                    String source = receivedMessage.getStringProperty(MsgPropertyID.Source);
                    System.out.println("Ricevuto messaggio di QueueStatus da: " + source);
                    ArrayList<QueueItemDTO> array = (ArrayList<QueueItemDTO>) receivedMessage.getObject();
                    Iterator<QueueItemDTO> it = array.iterator();
                    this.engine.getListOfResponse().add(array);
                }
            }
        }
        catch (JMSException e) {
            onException(e);
        }
    }

     private void checkIfReceiveAllReady() {
        Iterator<String> it = this.engine.getReplicaManagers().keySet().iterator();
        while(it.hasNext())
        {
            String tmp = it.next();
            if (this.engine.getReplicaManagers().get(tmp)!=null)
            {
                this.isReceivedAllReady = true;
            }
            else
            {
                this.isReceivedAllReady = false;
                break;
            }
        }
        if (this.isReceivedAllReady)
        {
            System.out.println("Ricevuto ready message da tutte le repliche");
            this.engine.getTimeout1().cancel();
            this.engine.getTimeout1().purge();
            setGlobalDecision();
        }
    }

     private void setGlobalDecision() {
        Iterator<String> it = this.engine.getReplicaManagers().keySet().iterator();
        while(it.hasNext())
        {
            String tmp = it.next();
            if (this.engine.getReplicaManagers().get(tmp)==1)
            {
                System.out.println("SetGlobalDecision. Replica: " + tmp + "con COMMIT");
                engine.setGLOBAL_DECISION("COMMIT");
            }
            else
            {
                System.out.println("SetGlobalDecision. Replica: " + tmp + "con ABORT");
                engine.setGLOBAL_DECISION("ABORT");
                break;
            }
        }
        System.out.println("Global decision settata a: " + engine.getGLOBAL_DECISION());
        engine.sendGlobalDecision();
    }


  //Meccanismo di callback per segnalare eventuali problemi
    @Override
    public void onException(JMSException ex){
        System.out.println("JMS Exception: " + ex.getMessage());
    }
}
