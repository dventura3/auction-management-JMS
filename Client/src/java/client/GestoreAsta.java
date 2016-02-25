/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author Daniela
 */
import java.util.Iterator;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.dto.*;

public class GestoreAsta implements Runnable {

    private TopicConnectionFactory connectionFactory;
    private Topic topic;

    Workflow wf;
    AuctionTYPE tipoAsta;
    Subscriber s;
    Publisher p;
    AuctionBid ab;
    VectorClock vectorClock;
    String IDclient;
    DefaultListModel model;

    public GestoreAsta(Workflow wf,AuctionTYPE tipoAsta,TopicConnectionFactory connectionFactory,Topic topic, DefaultListModel... model){
        this.wf = wf;
        this.tipoAsta = tipoAsta;
        //this.IDclient = MiddlewareMain.IDclient;
        this.connectionFactory = connectionFactory;
        this.topic = topic;
        p = new Publisher(MiddlewareMain.IDclient,connectionFactory,topic);
        s = new Subscriber(MiddlewareMain.IDclient,p,wf.getID(),connectionFactory,topic);
        this.ab = new AuctionBid();
        this.ab.setManagerId("-");
        if(model.length!=0){
            this.model=model[0];
        }
    }

    @Override
    public void run(){
        if(tipoAsta == AuctionTYPE.DescendingPriceAuction){
            this.astaAlRibasso();
        }
        else{
            this.astaAlRialzo();
        }
    }

    public double arrotondaOfferta(double offerta){
        offerta = offerta * 10;
        int troncato = ((Double)(offerta)).intValue() ;
        Double d = new Double(troncato);
        offerta = (d/10.0);
        return offerta;
    }

    //asta al ribasso:
    //parto da NumTaskDescriptor*1
    //dopo 10 secondi decremento settando come nuovo valore d'offerta --> numTaskDescriptor*0.9
    //dopo 10 secondi decremento settando come nuovo valore d'offerta --> numTaskDescriptor*0.8
    public AuctionBid astaAlRibasso(){
        double i = 1.0;
        double offerta;
        while((s.listAuctionBisIsEmpty()==true) && (i>=0.0)){
            try {
                //calcolo il valore di offerta
                offerta = arrotondaOfferta(wf.getTasks().size() * i);
                System.out.println("VALORE DI OFFERTA: "+offerta);
                //mando il messaggio di status
                p.sendStatus(AuctionTYPE.DescendingPriceAuction, wf.getID(), wf.getTasks().size(),offerta,ab.getManagerId());
                Thread.sleep(5000); //10sec
                i=i-0.1;
            } catch (InterruptedException ex) {
                Logger.getLogger(GestoreAsta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Aspetto altri 5 sec., in attesa di eventuali ritardatari con offerte più alte ");
        try {
            //aspetto un altro po, a causa di eventuali ritardatari:
            Thread.sleep(5000); //AuctionBid contente il Manager vincitore!
            //AuctionBid contente il Manager vincitore!
        } catch (InterruptedException ex) {
            Logger.getLogger(GestoreAsta.class.getName()).log(Level.SEVERE, null, ex);
        }
        ab = s.getAuctionBidWinner();
        if(!ab.getManagerId().equals("-")){
        //mando un messaggio di FINE ASTA per notificarlo a tutti i manager!
        p.sendEndAuction(AuctionTYPE.DescendingPriceAuction, ab.getWorkflowId(),ab.getNumTaskDescriptor(),ab.getPriceOffered(),ab.getManagerId());
        //posso (finalmente!) chiudere le connessioni di publisher e subscriber.
        }
        s.closeConnection();
        p.closeConnection();
        System.out.println("\n--------------> ASTA AL RIBASSO TERMINATA! <--------------\n");
        return ab;
    }

    //asta al rialzo:
    //il client semplicemente aspetta 60sec durante i quali si susseguiranno (<-- ahh che termini che uso! ahhh che sono colta!)
    //le offerta dei vari manager... alla fine 1 solo vincerà e sarà colui che ha fatto l'offerta più elevata!
    public AuctionBid astaAlRialzo(){
        double offerta = 0.0;

        ab.setPriceOffered(offerta);

        int numVincitori = 0;

        do{
            try {
                //calcolo il nuovo valore di offerta
                offerta = sumOfferta(offerta+0.1);

                //mando il primissimo messaggio qui (poi tutta la gestione di ricezione e invio dei messaggi è fatta all'interno del subscriber)
                p.sendStatus(AuctionTYPE.AscendingPriceAuction, wf.getID(), wf.getTasks().size(),offerta,ab.getManagerId());

                Thread.sleep(5000); //10sec
                
                //AuctionBid contente il Manager attualmente vincitore vincitore!
                ab = s.getAuctionBidWinner();
                Iterator<AuctionBid> it = s.getlistaDiBidVincitori().iterator();
                while(it.hasNext())
                {
                    AuctionBid tmp = it.next();
                    if(!model.contains("Manager: " + tmp.getManagerId() + " - Bid: " + tmp.getPriceOffered() + " - Workflow: " + tmp.getWorkflowId()))
                        model.addElement("Manager: " + tmp.getManagerId() + " - Bid: " + tmp.getPriceOffered() + " - Workflow: " + tmp.getWorkflowId());
                }
                
                numVincitori = s.numAttualiVincitori(offerta);
                if(numVincitori == 1){
                    for(int i=1; i<=3; i++){
                        Thread.sleep(2000); //2*3=6sec
                        if(s.numAttualiVincitori(offerta)==numVincitori){
                            System.out.println("Vincitore attuale dell'asta: " +ab.getManagerId() + ".... e " + i);
                        }
                        else{
                            //se sono qui, il numero di vincitori sarà cambiato
                            numVincitori = s.numAttualiVincitori(offerta);
                            //se ho più di un possibile vincitore, esco immediatamente dal ciclo
                            break;
                        }
                    }
                }

                //se al termine dei 6 secondi, il vincitore continua ad essere solo 1 --> esco fuori dal ciclo!
                //cioè NON mando più messaggi! l'asta si è chiusa
                if(numVincitori==1){
                    break;
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(GestoreAsta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(numVincitori>1 && offerta<1.0);
        if(numVincitori != 0){
        //mando un messaggio di FINE ASTA per notificarlo a tutti i manager!
        p.sendEndAuction(AuctionTYPE.AscendingPriceAuction, ab.getWorkflowId(),ab.getNumTaskDescriptor(),ab.getPriceOffered(),ab.getManagerId());
        //posso (finalmente!) chiudere le connessioni di publisher e subscriber.
        }
        s.closeConnection();
        p.closeConnection();
        System.out.println("\n--------------> ASTA AL RIALZO TERMINATA! <--------------\n");
        return ab;
    }

  public double sumOfferta(double offerta){
      offerta = (Math.round(offerta*10.0))/10.0;
      return offerta;
  }

}
