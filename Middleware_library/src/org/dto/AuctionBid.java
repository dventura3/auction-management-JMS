/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;

/**
 *
 * @author marcx87
 */
public class AuctionBid implements Serializable{
    private ActionTYPE action;
    private AuctionTYPE auctionType;
    private String managerId;
    private String managerIP;
    private String managerPort;
    private int workflowId;
    private int numTaskDescriptor;
    private double priceOffered;
    private VectorClock vectorClock;

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }
    
    public ActionTYPE getAction() {
        return action;
    }

    public void setAction(ActionTYPE action) {
        this.action = action;
    }

    public AuctionTYPE getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(AuctionTYPE auctionType) {
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

    @Override
    public String toString(){
        StringBuilder s  = new StringBuilder();
        s.append("\n");
        s.append("#-------- BID ------------#\n");
        s.append("TypeAucion: ").append(this.auctionType) .append("\n");
        s.append("ID-WorkFlow: ").append(this.workflowId).append("\n");
        s.append("Num-TaskDescriptor: ").append(this.numTaskDescriptor).append("\n");
        s.append("Manager ID(vincitore corrente): ").append(this.managerId).append("\n");
        s.append("Manager IP: ").append(this.managerIP).append("\n");
        s.append("Manager Port: ").append(this.managerPort).append("\n");
        s.append("prezzo Offerto: ").append(this.priceOffered).append("\n");
        s.append(this.vectorClock.toString());
        s.append("\n#-------------------------#"+"\n");
        s.append("\n");
        return s.toString();
    }
    
}
