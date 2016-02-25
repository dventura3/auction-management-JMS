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
 * Classe che permette di rappresentare un oggetto Bid
 * nel file di log
 * @author marcx87
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SingleBidAuction", propOrder = {
    "managerId",
    "workflowId",
    "action",
    "auctionType",
    "numTaskDescriptor",
    "managerIP",
    "managerPort",
    "priceOffered",
    "logicalClock"
})
public class SingleBidAuction {
    @XmlElement(required = true)
    protected  String action;
    @XmlElement(required = true)
    protected  String auctionType;
    @XmlElement(required = true)
    protected  String managerId;
    @XmlElement(required = true)
    protected String managerIP;
    @XmlElement(required = true)
    protected  String managerPort;
    @XmlElement(required = true)
    protected  int workflowId;
    @XmlElement(required = true)
    protected  int numTaskDescriptor;
    @XmlElement(required = true)
    protected  double priceOffered;
     @XmlElement(required = true)
    protected int logicalClock;

    public int getLogicalClock() {
        return logicalClock;
    }

    public void setLogicalClock(int logicalClock) {
        this.logicalClock = logicalClock;
    }
     
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleBidAuction other = (SingleBidAuction) obj;
        if ((this.managerId == null) ? (other.managerId != null) : !this.managerId.equals(other.managerId)) {
            return false;
        }
        if (this.workflowId != other.workflowId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.managerId != null ? this.managerId.hashCode() : 0);
        hash = 97 * hash + this.workflowId;
        return hash;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public String getManagerIP() {
        return managerIP;
    }

    public void setManagerIP(String managerIP) {
        this.managerIP = managerIP;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerPort() {
        return managerPort;
    }

    public void setManagerPort(String managerPort) {
        this.managerPort = managerPort;
    }

    public int getNumTaskDescriptor() {
        return numTaskDescriptor;
    }

    public void setNumTaskDescriptor(int numTaskDescriptor) {
        this.numTaskDescriptor = numTaskDescriptor;
    }

    public double getPriceOffered() {
        return priceOffered;
    }

    public void setPriceOffered(double priceOffered) {
        this.priceOffered = priceOffered;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }
}
