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

/**
 *Classe che permette di creare un file di Log contente
 * l'id di un determinato TM
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionManagerConfig", propOrder = {
 "transactionManagerId"
})
public class TransactionManagerConfig {
    /**
     * Id del TM
     */
    @XmlElement(required = true)
    protected String transactionManagerId;
     /**
     * Metodo che ritorna l'id del TM
     * @return Id del TM
     */
    public String getTransactionManagerId() {
        return transactionManagerId;
    }
     /**
     * Metodo che permette di settare l'id del TM
     * @param transactionManagerId Il nuovo id del TM
     */
    public void setTransactionManagerId(String transactionManagerId) {
        this.transactionManagerId = transactionManagerId;
    }
}
