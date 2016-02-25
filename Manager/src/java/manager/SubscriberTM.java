/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.xml.bind.JAXBException;
import org.dto.ResponseMessage;
import org.util.MessageTYPE;
import org.util.MsgPropertyID;

/**
 *
 * @author Daniela
 */
public class SubscriberTM implements MessageListener{

    private TopicSession session;
    private TopicPublisher pub;
    private ObjectMessage objMessage;

    public SubscriberTM(TopicConnection connection,Topic topic){
        super();
        try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            pub = session.createPublisher(topic);
            objMessage = session.createObjectMessage();
        } catch (JMSException ex) {
            Logger.getLogger(SubscriberTM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            try {
                ObjectMessage messageReceived = (ObjectMessage) message;
                ResponseMessage response = (ResponseMessage) messageReceived.getObject();
                String result = response.getMessage();
                //aggiorno il VC
                MainManager.incrementVectorClock(response.getVectorClock());
                //salvo il vectorClock nel file di LOG
                MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                if(result.equals("OK")){
                    ResponseMessage r = new ResponseMessage();
                    r.setMessage(String.valueOf(messageReceived.getIntProperty("Workflow")));
                    MainManager.queueTMP.add(WFQueueManager.returnWF(messageReceived.getIntProperty("Workflow")));
                    objMessage.setJMSType("Ack");
                    objMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.WorkflowResponse);
                    MainManager.incrementVectorClock(MainManager.vc);
                    //salvo il vectorClock nel file di LOG
                    MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                    r.setVectorClock(MainManager.vc);
                    objMessage.setObject(r);
                    pub.publish(objMessage);
                } else if(result.equals("ERROR")){
                    ResponseMessage r = new ResponseMessage();
                    r.setMessage(String.valueOf(messageReceived.getIntProperty("Workflow")));
                    objMessage.setJMSType("Nack");
                    objMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.WorkflowResponse);
                    MainManager.incrementVectorClock(MainManager.vc);
                    //salvo il vectorClock nel file di LOG
                    MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                    r.setVectorClock(MainManager.vc);
                    objMessage.setObject(r);
                    pub.publish(objMessage);
                }
            } catch (JAXBException ex) {
                Logger.getLogger(SubscriberTM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SubscriberTM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMSException ex) {
                Logger.getLogger(SubscriberTM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
