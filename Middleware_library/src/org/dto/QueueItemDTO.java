/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author marcx87
 */
public class QueueItemDTO implements Serializable, Comparable<QueueItemDTO>{
    private String ID;
    private Workflow workflow;
    private Date timestamp;
    private VectorClock vectorClock;
    private ArrayList<String> replicaManager;
    private RequestTYPE requestType;

    public RequestTYPE getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTYPE requestType) {
        this.requestType = requestType;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }

    public QueueItemDTO(String ID, Workflow workflow, Date timestamp){
        this.ID = ID;
        this.workflow = workflow;
        this.timestamp = timestamp;
        replicaManager = new ArrayList<String>();
    }

    public String getID(){
        return this.ID;
    }

    public Workflow getWorkflow(){
        return this.workflow;
    }

    public Date getTimestamp(){
        return this.timestamp;
    }

    public ArrayList<String> getReplicaManager(){
        return this.replicaManager;
    }

    public void setReplicaManager(ArrayList<String> rm){
        this.replicaManager = rm;
    }

    public int compareTo(QueueItemDTO o) {
        VectorComparison cp =  VectorClock.compare(this.vectorClock, o.vectorClock);
        if(cp == VectorComparison.GREATER){
            return 1;
        } else if(cp == VectorComparison.SMALLER){
            return -1;
        } else if(cp == VectorComparison.EQUAL){
            return 0;
        } else {
            return this.ID.compareTo(o.ID);
        }
    }
}
