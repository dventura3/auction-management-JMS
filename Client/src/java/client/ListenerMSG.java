/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author Daniela
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.xml.bind.JAXBException;
import org.dto.*;

public class ListenerMSG implements MessageListener,ExceptionListener {

    private TextMessage rcvdMessage;
    private ObjectMessage objMessage;

    private String subName;
    //private String messageSelector;
    private int IDWorkFlow;

    private Publisher p;

    private AuctionBid ab;//alla fine dell'asta (di qualunque tipo di asta sia) contiene sempre
                          //il bid con il manager vincitore

    private LinkedList<AuctionBid> listAB;

    public ListenerMSG(String subName,int IDWorkFlow,Publisher p){

        this.subName = subName;
        this.IDWorkFlow = IDWorkFlow;

        this.p = p;

        this.ab = new AuctionBid();
        this.listAB = new LinkedList<AuctionBid>();

    }

    @Override
    public void onMessage(Message message){
        try {
            if(message instanceof ObjectMessage){
                objMessage = (ObjectMessage) message;
                String type = objMessage.getJMSType();
                System.out.println("Arrivato un messaggio di tipo: " + type );
                if(type.equals("Bid")){
                    //BID ARRIVATO, adesso devo processarlo per vederne il contenuto al suo interno
                    AuctionBid abTMP = (AuctionBid)objMessage.getObject();
                     //a secondo che ho asta al ribasso(=1) o al rialzo (=0) eseguo due funzioni diverse
                    if(abTMP.getAuctionType().toString().equals("AstaRibasso")){
                        this.astaAlRibassoSubcriber(abTMP);
                    }
                    else if(abTMP.getAuctionType().toString().equals("AstaRialzo")){
                        this.astaAlRialzoSubcriber(abTMP);
                    }
                }
            }
        } catch (JMSException ex) {
            Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Meccanismo di callback per segnalare eventuali problemi
    @Override
    public void onException(JMSException ex){
        System.out.println("JMS Exception: " + ex.getMessage());
    }

  public void astaAlRibassoSubcriber(AuctionBid abTMP){
      //nell'asta al ribasso entro 10sec. possono arrivare differenti messaggi con dentro la stessa offerta.
      //questi messaggi devono essere ordinati per timestamp e preso quello con timestamp minore
      //siccome è arrivato un messaggio nuovo,devo aggiornare this.vectorClock
      if(abTMP.getWorkflowId()==this.IDWorkFlow){
            try {
                //essendo arrivato un nuovo messaggio, aggiorno il vectorClock
                MiddlewareMain.incrementVectorClock(abTMP.getVectorClock());
                //salvo il vectorClock nel file di LOG
                MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
                if (listAB.isEmpty()) {
                    listAB.add(abTMP);
                } else {
                    //ottengo l'offerta più alta fino ad ora fatta
                    double offertaPiuAlta = getOffertaMaggiore();
                    //verifico se l'attuale offerta più alta è <= di quella del Bid Arrivato
                    if (offertaPiuAlta <= abTMP.getPriceOffered()) {
                        listAB.add(abTMP);
                    }
                    //in caso contrario, vuol dire che è già arrivato un Bid con prezzo più alto!
                    //posso quindi scartare abTMP appena arrivato!
                }
                System.out.println("Asta al Ribasso! VectorClock Aggiornato e AuctionBid inserito nel vettore!");
            } catch (JAXBException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            }
      }
  }

  public void astaAlRialzoSubcriber(AuctionBid abTMP){
        //se l'idWF del Bid che è arrivato NON riguarda l'idWF del workFlow che sto attualmente considerando
        //vuol dire che è un messaggio "vecchio" e quindi scarto il messaggio.
        //inoltre il filtraggio dei BID che arrivano viene fatto sulla base del valore di offerta contenuto
        //dentro il bid stesso. Se abTMP.getPriceOffered()==this.offerta --> il bid viene scartato.
        if(abTMP.getWorkflowId()==this.IDWorkFlow){
            try {
                //essendo arrivato un nuovo messaggio, aggiorno il vectorClock
                MiddlewareMain.incrementVectorClock(abTMP.getVectorClock());
                //salvo il vectorClock nel file di LOG
                MiddlewareMain.clientLog.storeClientLogData(MiddlewareMain.IDclient, MiddlewareMain.vc);
                //appena arriva un messaggio, lo inserisco in lista, sarà processato solo successivamente
                listAB.add(abTMP);
                System.out.println("Asta al Rialzo! VectorClock Aggiornato e AuctionBid inserito nel vettore!");
            } catch (JAXBException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ListenerMSG.class.getName()).log(Level.SEVERE, null, ex);
            }
      }
  }

  public double arrotondaOfferta(double offerta){
      offerta = (Math.round(offerta*10.0))/10.0;
      return offerta;
  }

  public LinkedList<AuctionBid> getListaAuctionBid(){
      return this.listAB;
  }

  public AuctionBid calcolaBidWinner(){
      //nell'asta al ribasso, il vettore può contenere o bid con offerte differenti o bid con la stessa offerta massima
      //per cui innanzitutto riduco il cerchio ai soli bid con offerte più alte:
      double offertaPiuAlta = getOffertaMaggiore();
      LinkedList<AuctionBid> listaBidConOffertaPiuAlta = new LinkedList<AuctionBid>();
      Iterator iter = listAB.iterator();
      while(iter.hasNext()){
          AuctionBid auctionB = (AuctionBid)iter.next();
          if(auctionB.getPriceOffered() == offertaPiuAlta){
              listaBidConOffertaPiuAlta.add(auctionB); //usando "add", mantengo l'ordine dei msg ricevuti
          }
      }

      //se listaBidConOffertaPiuAlta.size() == 1 --> vuol dire che ho già trovato l'elemento vincitore
      //altrimenti, confronto i vectorClock!
      if(listaBidConOffertaPiuAlta.size() == 1){
          return listaBidConOffertaPiuAlta.getFirst();
      }

      //se sono qui, vuol dire che dentro il vettore ci sono più di 1 solo elemento
      //confronto i vectroClock
      if(!listaBidConOffertaPiuAlta.isEmpty()){
          AuctionBid abWinner = listaBidConOffertaPiuAlta.getFirst();
          Iterator it = listaBidConOffertaPiuAlta.iterator();
          while(it.hasNext()){
              AuctionBid bid = (AuctionBid)it.next();
              VectorComparison confronto = VectorClock.compare(abWinner.getVectorClock(), bid.getVectorClock());
              if(confronto==VectorComparison.EQUAL){
                  System.out.println("Confronto tra VectorClock: UGUALI");
              }
              if(confronto==VectorComparison.GREATER){
                  //vuol dire che abWinner.getVectorClock() > bid.getVectorClock()
                  abWinner = bid;
                  System.out.println("Confronto tra VectorClock: abTMP MAGGIORE di bid");
              }
              if(confronto==VectorComparison.SMALLER){
                  //vuol dire che abWinner.getVectorClock() < bid.getVectorClock()
                  System.out.println("Confronto tra VectorClock: abTMP MINORE di bid");
              }
              if(confronto==VectorComparison.SIMULTANEOUS){
                  //vettori concorrenti! vince chi è arrivato prima
                  int indexABWinner = listaBidConOffertaPiuAlta.indexOf(abWinner);
                  int indexBid = listaBidConOffertaPiuAlta.indexOf(bid);
                  if(indexBid<indexABWinner){
                      abWinner=bid;
                  }
                  System.out.println("Confronto tra VectorClock: CONCORRENTI");
              }
          }
          return abWinner;
      }
      return null;
  }

  public LinkedList<AuctionBid> calcolaBidWinnerList(){
      return this.listAB;
  }

  public double getOffertaMaggiore(){
      double offertaPiuAlta = 0.0;
      if(!listAB.isEmpty()){
          offertaPiuAlta = listAB.getFirst().getPriceOffered();
          Iterator iter = listAB.iterator();
          while(iter.hasNext()){
              AuctionBid auctionB = (AuctionBid)iter.next();
              if(auctionB.getPriceOffered() > offertaPiuAlta){
                  offertaPiuAlta = auctionB.getPriceOffered();
              }
          }
      }
      return offertaPiuAlta;
  }

//  public int numAttualiVincitori(){
//      int n=0;
//      double offertaPiuAlta = getOffertaMaggiore();
//      Iterator iter = listAB.iterator();
//      while(iter.hasNext()){
//          AuctionBid auctionB = (AuctionBid)iter.next();
//          if(auctionB.getPriceOffered() == offertaPiuAlta){
//              n++;
//          }
//      }
//      return n;
//  }

  public int numAttualiVincitoriPerOffertaPiuAlta(double offertaPiuAltaAttuale){
      int n=0;
      double offertaPiuAlta = getOffertaMaggiore();
      Iterator iter = listAB.iterator();
      while(iter.hasNext()){
          AuctionBid auctionB = (AuctionBid)iter.next();
          if(auctionB.getPriceOffered() == offertaPiuAlta){
              n++;
          }
      }
      if(offertaPiuAlta!=offertaPiuAltaAttuale){
          return 0;
      }
      return n;
  }
}
