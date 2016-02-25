/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.XMLGen;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Classe che permette di costruire un file di log contenente
 * le ultime offerte delle aste a cui il Manager a preso parte.
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuctionBidLog", propOrder = {
 "singleBidAuction"
})
public class AuctionBidLog {
    /**
     * Lista di offerte per le diverse aste a cui ha partecipato
     * il singolo Manager
     */
     protected ArrayList<SingleBidAuction> singleBidAuction;

     /**
      * Crea una nua lista di elementi se il campo sigleBidAuction non è
      * stato ancora inizializzato, oppure ritorna il valore di tale campo.
      * @return La lista di offerte delle diverse aste a cui ha partecipato
      * il singolo Manager
      */
    public ArrayList<SingleBidAuction> getSingleBidAuction() {
         if (singleBidAuction == null) {
             singleBidAuction= new ArrayList<SingleBidAuction>();
         }
         return this.singleBidAuction;
    }
}
