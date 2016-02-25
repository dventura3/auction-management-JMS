package replicamanager;

import java.io.Serializable;
import java.util.ArrayList;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import org.dto.QueueItemDTO;




//Classe che realizza il messaggio XML relativo allo stato dell asta 
// e si occupa di effettuare la pubblicazione

public class MessageService 
{

    private PublisherLogic pm;
    private String managerId;
    private TopicConnectionFactory tcf;
    private Topic topic;

    public MessageService(TopicConnectionFactory tcf, Topic topic, String Id){
        this.tcf = tcf;
        this.topic = topic;
        this.managerId = Id;
    }

    public boolean sendMessage(int type, String source)
    {
        pm = PublisherLogic.getInstance(tcf, topic, managerId);
        return pm.publish(type,source);
    }

    public boolean sendQueueStatus(ArrayList<QueueItemDTO> array, String source)
    {
        pm = PublisherLogic.getInstance(tcf, topic, managerId);
        return pm.publishQueueStatus(array, source);
    }
}
