/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.xml.bind.JAXBException;
import org.dao.ManagerLogDAOImpl;
import org.dto.VectorClock;
import org.dto.Workflow;
import org.util.Pair;

/**
 * Classe contenente il Thread Main del manager.
 * @author Daniela88, LuigiXIV, marcx87
 */
public class MainManager
{

    @Resource(name="jms/TopicConnectionFactory",mappedName = "jms/TopicConnectionFactory",type=javax.jms.TopicConnectionFactory.class)
    private static TopicConnectionFactory connectionFactory;
    @Resource(name="jms/Topic",mappedName = "jms/Topic",type=javax.jms.Topic.class)
    private static Topic topic;
    @Resource(name="jms/Topic2",mappedName = "jms/Topic2",type=javax.jms.Topic.class)
    private static Topic topic2;

    public static ManagerLogDAOImpl managerDAO = new  ManagerLogDAOImpl();
    public static String nomeManager;
    static int numPortaDefault = 9190;
    public static VectorClock vc;
    public static LinkedBlockingQueue<Workflow> queueTMP;
    public static TMproxy proxy;

    /**
     * Metodo main per far partire il thread Manager.
     * @param args lista di eventuali argomenti passati al momento dell'esecuzione del main
     */
    public static void main(String[] args)
    {
            try {
                queueTMP = new LinkedBlockingQueue<Workflow>();
                if (args.length > 4 || args.length == 3 || args.length == 1) {
                    System.out.println("Usage: java Manager -f [managerLog_NomeManager.xml] -p [nomePorta]");
                    System.exit(1);
                } else if (args.length == 4) {
                    try {
                        if (args[0].equals("-f")) {
                            Pair<String, VectorClock> pair = managerDAO.getManagerLogData(args[1]);
                            nomeManager = pair.getFirst();
                            vc = pair.getSecond();
                            System.out.println("Manager caricato da File XML con ID: "+nomeManager + " e porta: " +args[3]);
                            proxy = new TMproxy(connectionFactory.createTopicConnection(), topic2,topic);
                            int statoCoda = proxy.downloadStatusQueue();
                            if(statoCoda==0)
                                System.out.println("NON ci sono elementi da caricare in coda!");
                            else
                                System.err.println("Elementi caricati in coda!");
                            proxy.createSubscriberTM();
                            Manager m = new Manager(Integer.parseInt(args[3]), nomeManager, connectionFactory.createTopicConnection(), topic2);
                            Subscriber s = new Subscriber(nomeManager, InetAddress.getLocalHost().getHostAddress(), args[3], connectionFactory, topic);
                            m.start();
                        } else {
                            Pair<String, VectorClock> pair = managerDAO.getManagerLogData(args[3]);
                            nomeManager = pair.getFirst();
                            vc = pair.getSecond();
                            System.out.println("Manager caricato da File XML con ID: "+nomeManager + " e porta: " +args[1]);
                            proxy = new TMproxy(connectionFactory.createTopicConnection(), topic2,topic);
                            int statoCoda = proxy.downloadStatusQueue();
                            if(statoCoda==0)
                                System.out.println("NON ci sono elementi da caricare in coda!");
                            else
                                System.out.println("Elementi caricati in coda!");
                            proxy.createSubscriberTM();
                            Manager m = new Manager(Integer.parseInt(args[1]), nomeManager, connectionFactory.createTopicConnection(), topic2);
                            Subscriber s = new Subscriber(nomeManager, InetAddress.getLocalHost().getHostAddress(), args[1], connectionFactory, topic);
                            m.start();
                        }
                    } catch (JMSException ex) {
                        Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JAXBException ex) {
                        Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (args.length == 2) {
                    if (args[0].equals("-f")) {
                        try {
                            //significa che si vuole usare la porta di default
                            Pair<String, VectorClock> pair = managerDAO.getManagerLogData(args[1]);
                            nomeManager = pair.getFirst();
                            vc = pair.getSecond();
                            System.out.println("Manager caricato da File XML con ID: "+nomeManager + " e porta di DEFAULT[9190] ");
                            proxy = new TMproxy(connectionFactory.createTopicConnection(), topic2,topic);
                            int statoCoda = proxy.downloadStatusQueue();
                            if(statoCoda==0)
                                System.out.println("NON ci sono elementi da caricare in coda!");
                            else
                                System.err.println("Elementi caricati in coda!");
                            proxy.createSubscriberTM();
                            Manager m = new Manager(numPortaDefault, nomeManager, connectionFactory.createTopicConnection(), topic2);
                            Subscriber s = new Subscriber(nomeManager, InetAddress.getLocalHost().getHostAddress(), String.valueOf(numPortaDefault), connectionFactory, topic);
                            m.start();
                        } catch (JMSException ex) {
                            Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (JAXBException ex) {
                            Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (args[0].equals("-p")) {
                        try {
                            //significa che si vuole creare un nuovo manager il cui ID sarà generato in modo random
                            nomeManager = UUID.randomUUID().toString();
                            vc = new VectorClock();
                            System.out.println("Manager creato con ID random e porta: " + args[1]);
                            Manager m = new Manager(Integer.parseInt(args[1]), nomeManager, connectionFactory.createTopicConnection(), topic2);
                            Subscriber s = new Subscriber(nomeManager, InetAddress.getLocalHost().getHostAddress(), args[1], connectionFactory, topic);
                            m.start();
                            proxy = new TMproxy(connectionFactory.createTopicConnection(), topic2,topic);
                            proxy.createSubscriberTM();
                        } catch (JMSException ex) {
                            Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (args.length == 0) {
                    //significa che si vuole creare un nuovo manager il cui ID sarà generato in modo random
                    nomeManager = UUID.randomUUID().toString();
                    vc = new VectorClock();
                    System.out.println("Manager creato con ID random e porta di DEFAULT[9190] ");
                    Manager m = new Manager(numPortaDefault, nomeManager, connectionFactory.createTopicConnection(), topic2);
                    Subscriber s = new Subscriber(nomeManager, InetAddress.getLocalHost().getHostAddress(), String.valueOf(numPortaDefault), connectionFactory, topic);
                    m.start();
                    proxy = new TMproxy(connectionFactory.createTopicConnection(), topic2,topic);
                    proxy.createSubscriberTM();
                }
            } catch (JMSException ex) {
                Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(MainManager.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public static synchronized VectorClock getVectorClock(){
        return vc;
    }

    public static synchronized void incrementVectorClock(VectorClock vectorClockTMP){
        //operazioni da effettuare:
        //1) calcolo il massimo per i valori di IDprocesso corrispondenti
        //2) se ci sono IDProcessi che non conosco, li aggiungo al vectorClock
        //3) incremento di una unità il valore di clock legato al "subName"

        //1 + 2)
        vc = VectorClock.max(vc, vectorClockTMP);
        //3)
        vc.incrementClock(nomeManager);
    }

}
