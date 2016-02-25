/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TMQueueManager;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author marcx87
 */
public class AckBackupMessageListener implements MessageListener{
    private AckManager ackmanager;

    public AckBackupMessageListener(AckManager ackManager) {
        this.ackmanager = ackManager;
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("ACK dai Backup arrivato");
       if(!ackmanager.isAcked()){
           ackmanager.setAcked(true);
       }
    }

}
