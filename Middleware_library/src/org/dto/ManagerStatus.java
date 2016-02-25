/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author marcx87
 */
public class ManagerStatus implements Serializable{
    private VectorClock vectorClock;
    private ArrayList<QueueItemDTO> items;

    public ArrayList<QueueItemDTO> getItems() {
        return items;
    }

    public void setItems(ArrayList<QueueItemDTO> items) {
        this.items = items;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }
    
}
