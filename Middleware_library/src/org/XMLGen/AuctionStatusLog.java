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
 *Classe che permette di costruire un file di Log contenente lo stato
 * delle aste indette dal singolo Client.
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuctionStatusLog", propOrder = {
 "singleStatusAuction"
})
public class AuctionStatusLog {
    /**
     * Lista degli stati delle aste indette dal Client
     */
    protected ArrayList<SingleStatusAuction> singleStatusAuction;
    /**
     * Crea una lista di elementi se il campo singleStatusAuction non è
     * stato ancora inizializzato, oppure ritorna il valore di tale campo.
     * @return La lista degli stati delle aste indette dal Client
     */
    public ArrayList<SingleStatusAuction> getSingleStatusAuction() {
         if (singleStatusAuction == null) {
             singleStatusAuction= new ArrayList<SingleStatusAuction>();
         }
         return this.singleStatusAuction;
    }
}
