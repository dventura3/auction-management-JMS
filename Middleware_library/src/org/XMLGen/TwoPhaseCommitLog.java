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
 * della transazione.
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TwoPhaseCommitLog", propOrder = {
 "singleQueue"
})
public class TwoPhaseCommitLog {
    protected ArrayList<SingleQueueTwoPC> singleQueue;

  /**
  * Gets the value of the singleQueue property.
  * <p>
  * This accessor method returns a reference to the live list,
  * not a snapshot. Therefore any modification you make to the
  * returned list will be present inside the JAXB object.
  * This is why there is not a <CODE>set</CODE> method for the singleQueue property.
   * </p>
  * <p>
  * For example, to add a new item, do as follows:
  * <pre>
  *    getSingleQueue().add(newItem);
  * </pre>
   * </p>
  * <p>
  * Objects of the following type(s) are allowed in the list
  * {@link SingleQueueTwoPC }
   * </p>
  */
    public ArrayList<SingleQueueTwoPC> getSingleQueue() {
         if (singleQueue == null) {
             singleQueue = new ArrayList<SingleQueueTwoPC>();
         }
         return this.singleQueue;
    }
}
