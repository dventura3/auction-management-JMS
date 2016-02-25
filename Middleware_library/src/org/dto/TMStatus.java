/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;
import java.util.LinkedList;
import org.util.TwoPCState;

/**
 *
 * @author marcx87
 */
public class TMStatus implements Serializable{
    private VectorClock vectorClock;
    private TwoPCState twoPCState;
    private QueueItemDTO currentItemTransaction;
    private LinkedList<QueueItemDTO> bufferedItems;

    public LinkedList<QueueItemDTO> getBufferedItems() {
        return bufferedItems;
    }

    public void setBufferedItems(LinkedList<QueueItemDTO> bufferedItems) {
        this.bufferedItems = bufferedItems;
    }

    public QueueItemDTO getCurrentItemTransaction() {
        return currentItemTransaction;
    }

    public void setCurrentItemTransaction(QueueItemDTO currentItemTransaction) {
        this.currentItemTransaction = currentItemTransaction;
    }

    
    public TwoPCState getTwoPCState() {
        return twoPCState;
    }

    public void setTwoPCState(TwoPCState twoPCState) {
        this.twoPCState = twoPCState;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }
}
