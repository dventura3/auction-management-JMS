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
 * l'id di una determinata replica
 * @author marcx87
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReplicaConfig", propOrder = {
 "replicaId"
})
public class ReplicaConfig {
    /**
     * Id della replica
     */
    @XmlElement(required=true)
    protected String replicaId;
    /**
     * Metodo che ritorna l'id della replica
     * @return Id della replica
     */
    public String getReplicaId() {
        return replicaId;
    }
    /**
     * Metodo che permette di settare l'id della replica
     * @param replicaId Il nuovo id della replica
     */
    public void setReplicaId(String replicaId) {
        this.replicaId = replicaId;
    }
}
