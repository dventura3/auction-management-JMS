/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

/**
 *
 * @author Daniela
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import org.dto.*;
import org.dao.AuctionDaoImpl;

public class ListenerMSG implements MessageListener,ExceptionListener {

    private TextMessage rcvdMessage; //Messaggi pubblicati e ricevuti dal publisher
    private ObjectMessage objMessage;
    private String subIP;
    private String subPort;

    private PublisherAuction p; // il manager è subscriber degli "status" ma publisher dei "bid"

    //private ArrayList<AuctionStatus> as;
    private HashMap<Integer,AuctionStatus> hashAS; //key=IDWorkflow, Value=ultimo AuctionStatus ricevuto.

    AuctionDaoImpl adi;

    public ListenerMSG(String nome,String ip, String port,PublisherAuction p){
        try {
            rcvdMessage = null;
            subIP = ip;
            subPort = port;

            adi = new AuctionDaoImpl();
            hashAS = new HashMap<Integer, AuctionStatus>();
            ArrayList<AuctionStatus> as = adi.getAllAuctionStatus(MainManager.nomeManager); //inserisco dentro l'arrayList eventuali Status presi dal file di LOG
            //se però action=="EndStatus"... rimuovo tale elemento dal vettore.
            Iterator it = as.iterator();
            while(it.hasNext()){
                AuctionStatus auctionTMP = (AuctionStatus)it.next();
                if(!auctionTMP.getAction().toString().equals("EndAuction")){
                    hashAS.put(auctionTMP.getWorkflowId(), auctionTMP);
                }else{
                    adi.clearAuctionStatus(auctionTMP, MainManager.nomeManager);
                }
            }

            this.p = p;
            
        } catch (JAXBException ex) {
            Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message){
        try {
            if(message instanceof ObjectMessage){
                objMessage = (ObjectMessage) message;
                String type = objMessage.getJMSType();
                if(type.equals("Status")){
                    //BID ARRIVATO, adesso devo processarlo per vederne il contenuto al suo interno
                    AuctionStatus asTMP = (AuctionStatus)objMessage.getObject();

                    //scorro la lista di Status ... se l'attuale "StatoTMP" non è presente in lista, vuol dire che
                    //è il primo messagio inviato dal client per dare inizio all'asta --> quindi inserisco statoTMP nel vettore
                    //la verifica viene fatta in base all'id del WorkFlow.
                    boolean verificaStatoTMP = false;
                    Iterator<Integer> iter = hashAS.keySet().iterator();
                    while(iter.hasNext()){
                        Integer IDwf = iter.next();
                        AuctionStatus ast = hashAS.get(IDwf);
                        if(IDwf==asTMP.getWorkflowId()){
                            //aggiorno lo Status dentro il vettore
                            asTMP.setRandomValue(ast.getRandomValue()); //mantengo lo stesso valore Random
                            hashAS.put(IDwf, asTMP);
                            //setto a "true" verificaStatoTMP in modo da sapere che lo Status su questo WorkFlow è arrivato nel passato
                            verificaStatoTMP = true;
                        }
                    }
                    if(verificaStatoTMP==false){
                        //se non ho già trovato statoTMP dentro il vettore --> verificaStatoTMP = false
                        //allora devo creare in numero random tra 0.1 e 1.0
                        //e inserire tale statoTMP dentro la LinkedList
                        asTMP.generaRandomValue();
                        System.out.println("\nValore Random GENERATO: "+asTMP.getRandomValue()+" da parte del manager: "+ MainManager.nomeManager);
                        hashAS.put(asTMP.getWorkflowId(), asTMP);
                    }

                    //Siccome è arrivato un nuovo messaggio, devo aggiornare il vectorClock
                    MainManager.incrementVectorClock(asTMP.getVectorClock());

                    //salvo lo Status ricevuto dentro il file di LOG
                    adi.LogAuctionStatus(asTMP, MainManager.nomeManager);
                    //salvo pure il vectroClock del manager
                    MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);

                    //stampo a video lo status ricevuto:
                    System.out.println(asTMP.toString());

                    //in base al valore di "typeAuction" so se il client ha richiesto asta al rialzo o al ribasso
                    //typeAuction==1 --> asta al ribasso
                    //typeAuction==0 --> asta al rialzo
                    if(asTMP.getTypeAuction().toString().equals("AstaRibasso")){
                        boolean verifica = this.astaAlRibasso(asTMP);
                        if(verifica==false){
                            System.out.println("AstaAlRibasso: Non è ancora arrivato il mio turno di offerta! Sono il Manager: " + MainManager.nomeManager);
                        }
                        else{
                            System.out.println("AstaAlRibasso: offerta FATTA da parte del Manager: " + MainManager.nomeManager);
                        }
                    }
                    else if(asTMP.getTypeAuction().toString().equals("AstaRialzo")){
                        boolean verifica = this.astaAlRialzo(asTMP);
                        if(verifica==false){
                            System.out.println("AstaAlRialzo: Non posso più fare offerte perchè il prezzo è troppo ALTO! Sono il Manager: " + MainManager.nomeManager);
                        }
                        else{
                            System.out.println("AstaAlRialzo: offerta FATTA da parte del Manager: " + MainManager.nomeManager);
                        }
                    }
                }
                if(type.equals("EndAuction")){
                    //BID ARRIVATO, adesso devo processarlo per vederne il contenuto al suo interno
                    AuctionStatus asTMP = (AuctionStatus)objMessage.getObject();
                    
                    //Siccome è arrivato un nuovo messaggio, devo aggiornare il vectorClock
                    MainManager.incrementVectorClock(asTMP.getVectorClock());
                    //salvo pure il vectroClock del manager
                    MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);

                    //salvo lo Status ricevuto dentro il file di LOG
                    adi.LogAuctionStatus(asTMP, MainManager.nomeManager);

                    //elimino lo Status dalla ArrayList
                    Iterator<Integer> it = hashAS.keySet().iterator();
                    AuctionStatus toRemove = null;
                    while(it.hasNext()){
                        Integer IDwf = it.next();
                        AuctionStatus abTwo = hashAS.get(IDwf);
                        if(IDwf == asTMP.getWorkflowId()){
                            toRemove = abTwo;
                            break;
                            //as.remove(abTwo);
                        }
                    }
                    if(toRemove!=null)
                        hashAS.remove(toRemove.getWorkflowId());
                    if(asTMP.getCurrentWinner().equals(MainManager.nomeManager))
                        System.out.println("\n"+MainManager.nomeManager+": Sono il manager vincitore!\n");
                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException e) {
            onException(e);
        }
    }

    //Meccanismo di callback per segnalare eventuali problemi
    @Override
    public void onException(JMSException ex){
        System.out.println("JMS Exception: " + ex.getMessage());
    }

    public boolean astaAlRibasso(AuctionStatus asTMP){
        System.out.println("Asta al RIBASSO!");
        //verifico se il currentPrice è uguale al prezzo che il manager(this) vuole offrire (valueRandom*TaskDescriptor)
        //se currentPrice==valueRandom*numTaskDesciptor --> mando un bid al client
        //se currentPrice!=valueRandom*numTaskDesciptor --> non faccio nulla (aspetto che arrivi un nuovo
        //                                 status con il currentPrice = al mio valueRandom --> ma NON è detto che ciò accadrà)
        if(asTMP.getCurrentPrice()==(asTMP.getRandomValue()*asTMP.getNumTaskDescriptor())){
            try {
                //mando un nuovo messaggio, quindi incremento il valore del clock
                MainManager.incrementVectorClock(MainManager.vc);
                //salvo pure il vectroClock del manager
                MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                p.sendBid(asTMP.getClientId(), asTMP.getTypeAuction(), asTMP.getWorkflowId(), asTMP.getNumTaskDescriptor(), asTMP.getRandomValue() * asTMP.getNumTaskDescriptor(), MainManager.vc);
                System.out.println(MainManager.nomeManager + ": Faccio un'offerta paria a :" + (asTMP.getRandomValue() * asTMP.getNumTaskDescriptor()) + " Con valore di VectorClock: " + MainManager.vc.toString());
                return true;
            } catch (JAXBException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public boolean astaAlRialzo(AuctionStatus asTMP){
        System.out.println("Asta al RIALZO!");
        //asTMP.getRandomValue() --> è la soglia MASSIMA che non posso superare!
        //se asTMP.getRandomValue() < asTMP.getCurrentPrice() --> NON posso fare offerte!
        //se asTMP.getRandomValue() >= asTMP.getCurrentPrice() --> faccio una offerta pari al prezzo corrente
        if(asTMP.getRandomValue() >= asTMP.getCurrentPrice()){
            try {
                //mando un nuovo messaggio, quindi incremento il valore del clock
                MainManager.incrementVectorClock(MainManager.vc);
                //salvo pure il vectroClock del manager
                MainManager.managerDAO.storeManagerLogData(MainManager.nomeManager, MainManager.vc);
                p.sendBid(asTMP.getClientId(), asTMP.getTypeAuction(), asTMP.getWorkflowId(), asTMP.getNumTaskDescriptor(), asTMP.getCurrentPrice(), MainManager.vc);
                System.out.println(MainManager.nomeManager + ": Faccio un'offerta paria a :" + asTMP.getCurrentPrice() + " Con valore di VectorClock: " + MainManager.vc.toString());
                return true;
            } catch (JAXBException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

}
