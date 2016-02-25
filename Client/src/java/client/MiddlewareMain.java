/*
 * MiddlewareMain.java
 */

package client;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.xml.bind.JAXBException;
import org.dao.ClientLogDAOImpl;
import org.dto.VectorClock;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.util.Pair;

/**
 * The main class of the application.
 */
public class MiddlewareMain extends SingleFrameApplication {

    @Resource(mappedName = "jms/TopicConnectionFactory")
    private static TopicConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/Topic")
    private static Topic topic;

    public static ClientLogDAOImpl clientLog = new ClientLogDAOImpl();
    public static final Object sync = new Object();
    public static VectorClock vc;
    public static String IDclient;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new UI(this,connectionFactory,topic ));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of MiddlewareMain
     */
    public static MiddlewareMain getApplication() {
        return Application.getInstance(MiddlewareMain.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(MiddlewareMain.class, args);

        if(args.length>1){
            System.out.println("Usage: java Manager [NomeFileConfigurazione.xml]");
            System.exit(1);
        } else if(args.length == 1){
            try {
                //prendo il nome e il vectro clock dal file di configurazione
                Pair<String, VectorClock> pair = clientLog.getClientLogData(args[0]);
                IDclient = pair.getFirst();
                vc = pair.getSecond();
            } catch (JAXBException ex) {
                Logger.getLogger(MiddlewareMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MiddlewareMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(args.length < 1){
            //se sono qui, creo un nuovo client con nome "random"
            IDclient = UUID.randomUUID().toString();
            vc = new VectorClock();
        }
    }

    public static synchronized void incrementVectorClock(VectorClock vectorClockTMP){
        vc = VectorClock.max(vc, vectorClockTMP);
        vc.incrementClock(IDclient);
        System.out.println("VectorClock aggiornato dopo l'arrivo di un nuovo msg: " + vc.toString());
    }

}
