/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.XMLGen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Classe che permette di mappare lo status dell'asta in un file di log
 * @author marcx87
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SingleStatusAuction", propOrder = {
    "clientId",
    "workflowId",
    "action",
    "typeAuction",
    "numTaskDescriptor",
    "randomValue",
    "currentPrice",
    "currentWinner",
    "logicalClock"
})
public class SingleStatusAuction {
    @XmlElement(required = true)
    protected  String action;
    @XmlElement(required = true)
    protected  String typeAuction;
    @XmlElement(required = true)
    protected  String clientId;
    @XmlElement(required = true)
    protected  int workflowId;
    @XmlElement(required = true)
    protected  int numTaskDescriptor;
    @XmlElement(required = true)
    protected  double currentPrice;
    @XmlElement(required = true)
    protected  String currentWinner;
    @XmlElement(required = true)
    protected  double randomValue;
    @XmlElement(required = true)
    protected int logicalClock;

    public int getLogicalClock() {
        return logicalClock;
    }

    public void setLogicalClock(int logicalClock) {
        this.logicalClock = logicalClock;
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCurrentWinner() {
        return currentWinner;
    }

    public void setCurrentWinner(String currentWinner) {
        this.currentWinner = currentWinner;
    }

    public int getNumTaskDescriptor() {
        return numTaskDescriptor;
    }

    public void setNumTaskDescriptor(int numTaskDescriptor) {
        this.numTaskDescriptor = numTaskDescriptor;
    }

    public double getRandomValue() {
        return randomValue;
    }

    public void setRandomValue(double randomValue) {
        this.randomValue = randomValue;
    }
    
    public String getTypeAuction() {
        return typeAuction;
    }

    public void setTypeAuction(String typeAuction) {
        this.typeAuction = typeAuction;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleStatusAuction other = (SingleStatusAuction) obj;
        if ((this.clientId == null) ? (other.clientId != null) : !this.clientId.equals(other.clientId)) {
            return false;
        }
        if (this.workflowId != other.workflowId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.clientId != null ? this.clientId.hashCode() : 0);
        hash = 43 * hash + this.workflowId;
        return hash;
    }
}
