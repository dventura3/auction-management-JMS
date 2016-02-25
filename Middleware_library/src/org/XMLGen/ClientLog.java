/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.XMLGen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.dto.VectorClock;

/**
 *Classe che permette di costruire un file di Log nel Client in cui vengono memorizzati
 * il clientId e il vectorClock attuale.
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientLog", propOrder = {
 "clientId",
 "vectorClock"
})
public class ClientLog {
    /**
     * Id del client
     */
    @XmlElement(required = true)
    protected String clientId;
    /**
     * Vector Clock corrente
     */
    protected VectorClock vectorClock;

    /**
     * Metodo che ritorna l'identificativo del client
     * @return L'identificativo del client
     */
    public String getClientId() {
        return clientId;
    }
    /**
     * Metodo che permette di settare l'identificativo del client
     * @param clientId Il nuovo identificativo del client
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    /**
     * Metodo che ritorna il VectorClock del client
     * @return VectorClock del client
     */
    public VectorClock getVectorClock() {
        return vectorClock;
    }
    /**
     * Metodo che permette di settare un nuovo VectorClock per il client
     * @param vectorClock Il nuovo VectorClock del client
     */
    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }
    /**
     * Metodo che permette di stabilire se due oggetti ClientLog sono uguali
     * @param obj l'oggetto da confrontare
     * @return true se i due oggetti sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientLog other = (ClientLog) obj;
        if ((this.clientId == null) ? (other.clientId != null) : !this.clientId.equals(other.clientId)) {
            return false;
        }
        if (this.vectorClock != other.vectorClock && (this.vectorClock == null || !this.vectorClock.equals(other.vectorClock))) {
            return false;
        }
        return true;
    }
    /**
     * Metodo che permette di ottenere l'hashCode di un oggetto ClientLog
     * @return L'hasCode di un oggetto ClientLog
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.clientId != null ? this.clientId.hashCode() : 0);
        return hash;
    }
}
