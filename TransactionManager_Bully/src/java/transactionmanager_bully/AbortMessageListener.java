/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transactionmanager_bully;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * MessageListener implementation used to sense abort commands
 * @author marcx87
 */
public class AbortMessageListener implements MessageListener {

    private BullyEngine bullyEngine;

    public AbortMessageListener(BullyEngine bullyEngine){
        this.bullyEngine = bullyEngine;
    }

    @Override
    public void onMessage(Message message) {
            // bullyEngine.setAbort(true);
            bullyEngine.getElection().setAbort(true);
            System.out.println("ABORT message received.");
    }
}