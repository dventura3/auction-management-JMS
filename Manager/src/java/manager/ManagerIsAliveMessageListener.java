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
class ManagerIsAliveMessageListener implements MessageListener {

    private TopicSession session;
    private TopicPublisher pub;
    private ObjectMessage objMessage;

    public ManagerIsAliveMessageListener(TopicConnection connection, Topic topic) {

        super();
        try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            pub = session.createPublisher(topic);
            objMessage = session.createObjectMessage();
        } catch (JMSException ex) {
            Logger.getLogger(ManagerIsAliveMessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            try {
                //quando ricevo MSG di tipo "IsAlive" :
                ObjectMessage messageReceived = (ObjectMessage) message;
                ResponseMessage response = (ResponseMessage) messageReceived.getObject();
                String result = response.getMessage(); //conterrà l'ID del WF di cui si sta chiedendo notizie
                //aggiorno il VC
                MainManager.incrementVectorClock(response.getVectorClock());
                //salvo il vectorClock nel file di LOG
                MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);

                //mando un msg di WAIT :
                ResponseMessage r = new ResponseMessage();
                r.setMessage(result);
                MainManager.incrementVectorClock(response.getVectorClock());
                r.setVectorClock(MainManager.vc);
                objMessage.setJMSType("Wait");
                objMessage.setIntProperty(MsgPropertyID.Type, MessageTYPE.WorkflowResponse);
                //salvo il vectorClock nel file di LOG
                MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                pub.publish(objMessage);
                
            } catch (JAXBException ex) {
                Logger.getLogger(ManagerIsAliveMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ManagerIsAliveMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMSException ex) {
                Logger.getLogger(ManagerIsAliveMessageListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
