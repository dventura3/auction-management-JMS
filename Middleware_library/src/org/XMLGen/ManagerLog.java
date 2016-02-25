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
 *Classe che permette di costruire un file di Log nel Manager in cui vengono memorizzati
 * il managerId e il vectorClock attuale.
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagerLog", propOrder = {
 "managerId",
 "vectorClock"
})
public class ManagerLog {
    /**
     * Id del manager
     */
    @XmlElement(required = true)
    protected String managerId;
    /**
     * VectorClock corrente
     */
    protected VectorClock vectorClock;
    /**
     * Metodo che ritorna l'identificativo del manager
     * @return L'identificativo del manager
     */
    public String getManagerId() {
        return managerId;
    }
    /**
     * Metodo che permette di settare l'identificativo del manager
     * @param managerId Il nuovo identificativo del manager
     */
    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    /**
     * Metodo che ritorna il VectorClock del manager
     * @return VectorClock del manager
     */
    public VectorClock getVectorClock() {
        return vectorClock;
    }
    /**
     * Metodo che permette di settare un nuovo VectorClock per il manager
     * @param vectorClock Il nuovo VectorClock del manager
     */
    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }
    /**
     * Metodo che permette di stabilire se due oggetti ManagerLog sono uguali
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
        final ManagerLog other = (ManagerLog) obj;
        if ((this.managerId == null) ? (other.managerId != null) : !this.managerId.equals(other.managerId)) {
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
        int hash = 3;
        hash = 67 * hash + (this.managerId != null ? this.managerId.hashCode() : 0);
        return hash;
    }
}
