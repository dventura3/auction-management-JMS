/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.XMLGen;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Classe che permette di mappare un oggetto SingleQueue
 * nel file di log utile a stabilire l'andamento di una transazione
 * @author marcx87
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkflowReceived", propOrder = {
    "twoPCState",
    "replicaManagers",
    "queueID",
    "lastUpdateTime",
    "workflows",
    "requestType"
})
public class SingleQueueTwoPC {
    @XmlElement(required = true)
    protected String queueID;
    protected ArrayList<QueueItem> workflows;
    protected ArrayList<String> replicaManagers;
    @XmlElement(required = true)
    protected String lastUpdateTime;
    @XmlElement(required = true)
    protected String requestType;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    /**
     * Stato attuale del protocollo 2 phase commit.
     */
    @XmlElement(required = true)
    protected String twoPCState;
    /**
     * Gets the value of the queueID property.
     * @return
     *     possible object is
     *     {@link Integer }
     */
     /**
  * Gets the value of the workflows property.
  * <p>
  * This accessor method returns a reference to the live list,
  * not a snapshot. Therefore any modification you make to the
  * returned list will be present inside the JAXB object.
  * This is why there is not a <CODE>set</CODE> method for the workflows property.
    * </p>
  * <p>
  * For example, to add a new item, do as follows:
  * <pre>
  *    getQueueItem().add(newItem);
  * </pre>
    * </p>
  * <p>
  * Objects of the following type(s) are allowed in the list
  * {@link QueueItem }
    * </p>
  */
    public ArrayList<QueueItem> getWorkflows() {
         if (workflows == null) {
             workflows = new ArrayList<QueueItem>();
         }
         return this.workflows;
    }

    public ArrayList<String> getReplicaManagers(){
        if (replicaManagers == null) {
             replicaManagers = new ArrayList<String>();
         }
         return this.replicaManagers;
    }
    /**
     * Gets the value of the queueID property.
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getQueueID() {
        return queueID;
    }

    /**
     * Sets the value of the queueID property.
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public void setQueueID(String value) {
        this.queueID = value;
    }

    /**
     * Gets the value of the lastUpdateTime property.
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Sets the value of the lastUpdateTime property.
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public void setLastUpdateTime(String value) {
        this.lastUpdateTime = value;
    }

   

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.queueID != null ? this.queueID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleQueueTwoPC other = (SingleQueueTwoPC) obj;
        if ((this.queueID == null) ? (other.queueID != null) : !this.queueID.equals(other.queueID)) {
            return false;
        }
        return true;
    }
    /**
     * Ritorna lo stato corrente del protocollo 2 phase commit.
     * @return Stato del protocollo.
     */
    public String getTwoPCState(){
        return this.twoPCState;
    }
    /**
    * Setta lo stato corrente del protocollo 2 phase commit.
    * @param twoPCState Stato corrente del protocollo 2 phase commit.
    */
    public void setTwoPCState(String twoPCState){
        this.twoPCState = twoPCState;
    }
}
