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
 * Classe che permette di rappresentare un oggetto complesso workflow nel
 * database xml.
 * @author marcx87
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Workflow", propOrder = {
    "ID",
    "tasks"
})
public class QueueItem {
    /**
     * Struttura dati contenente i Task del Workflow.
     */
    protected ArrayList<TaskItem> tasks;
    /**
     * Identificativo del Workflow.
     */
    @XmlElement(required = true)
    protected int ID;
    /**
     * Gets the value of the queueID property.
     * @return
     *     possible object is
     *     {@link Integer }
     */
    public int getID() {
        return ID;
    }
    /**
     * Sets the value of the queueID property.
     * @param value
     *     allowed object is
     *     {@link Integer }
     */
    public void setID(int value) {
        this.ID = value;
    }
      /**
      * Gets the value of the tasks property.
      * <p>
      * This accessor method returns a reference to the live list,
      * not a snapshot. Therefore any modification you make to the
      * returned list will be present inside the JAXB object.
      * This is why there is not a <CODE>set</CODE> method for the tasks property.
      *</p>
      * <p>
      * For example, to add a new item, do as follows:
      * <pre>
      *    getQueueItem().add(newItem);
      * </pre>
      *</p>
      * <p>
      * Objects of the following type(s) are allowed in the list
      * {@link TaskItem }
      * </p>
      */
    public ArrayList<TaskItem> getTasks() {
         if (tasks == null) {
             tasks = new ArrayList<TaskItem>();
         }
         return this.tasks;
    }
    /**
     * Metodo che permette di ottenere l'hashCode QueueItem di un oggetto
     * @return L'hasCode di un oggetto ClientLog
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.ID;
        return hash;
    }
    /**
     * Metodo che permette di stabilire se due oggetti QueueItem sono uguali
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
        final QueueItem other = (QueueItem) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
}
