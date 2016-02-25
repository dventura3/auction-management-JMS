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
public class AuctionStatus implements Serializable{
    private ActionTYPE action;
    private AuctionTYPE typeAuction;
    private String clientId;
    private int workflowId;
    private int numTaskDescriptor;
    private double currentPrice;
    private String currentWinner;
    private double randomValue;
    private VectorClock vectorClock;

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(VectorClock timeStamp) {
        this.vectorClock = timeStamp;
    }
    
    public void setRandomValue(double randomValue) {
        this.randomValue = randomValue;
    }

    public double getRandomValue() {
        return randomValue;
    }

    public ActionTYPE getAction() {
        return action;
    }

    public String getClientId() {
        return clientId;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getCurrentWinner() {
        return currentWinner;
    }

    public int getNumTaskDescriptor() {
        return numTaskDescriptor;
    }

    public AuctionTYPE getTypeAuction() {
        return typeAuction;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setAction(ActionTYPE action) {
        this.action = action;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setCurrentWinner(String currentWinner) {
        this.currentWinner = currentWinner;
    }

    public void setNumTaskDescriptor(int numTaskDescriptor) {
        this.numTaskDescriptor = numTaskDescriptor;
    }

    public void setTypeAuction(AuctionTYPE typeAuction) {
        this.typeAuction = typeAuction;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

     public void generaRandomValue(){
        this.randomValue = Math.random()* 0.9 + 0.1; //genero un valore random tra 0.1 e 1.0
        //per evitare di avere millemila cifre dopo la virgola:
        //Supponiamo che this.valueRandom=0.6984829349
        this.randomValue = this.randomValue * 10; //this.valueRandom = 6.984829349
        int troncato = ((Double)(this.randomValue)).intValue() ; // troncato = 6
        Double d = new Double(troncato); // d = 6.0
        this.randomValue = (d/10.0); //this.valueRandom = 0.6
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
                s.append("\n");
        if(this.action.toString().equals("Status")){
            s.append("#-------- STATUS ------------#"+"\n");
        }else if(this.action.toString().equals("EndAuction")){
            s.append("#-------- EndAuction ------------#"+"\n");
        }
        s.append("TypeAucion: ").append(this.typeAuction) .append("\n");
        s.append("ClientID: ").append(this.clientId).append("\n");
        s.append("ID-WorkFlow: ").append(this.workflowId).append("\n");
        s.append("Num-TaskDescriptor: ").append(this.numTaskDescriptor).append("\n");
        s.append("Prezzo Corrente: ").append(this.currentPrice).append("\n");
        s.append("Vincitore Corrente: ").append(this.currentWinner).append("\n");
        s.append(this.vectorClock.toString());
        s.append("\nValore Random: ").append(this.randomValue).append("\n");
        s.append("#----------------------------#"+"\n");
        s.append("\n");
        return s.toString();
    }
}
