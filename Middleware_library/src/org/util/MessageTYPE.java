/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

/**
 *
 * @author marcx87
 */
public final class MessageTYPE {
    /**
     * Attributo che definisce lo stato di prepare da parte di un TM.
     */
    public static final int Ping = 0;
    /**
     * Attributo che definisce lo stato di ready da parte di un RM.
     */
    public static final int Pong = 1;
    /**
     * Attributo che definisce lo stato di not ready da parte di un RM.
     */
    public static final int Election = 2;
    /**
     * Attributo che definisce lo stato di global commit da parte di un TM.
     */
    public static final int Abort = 3;

    public static final int Coordinator = 4;

    public static final int Status = 5;

    public static final int QueueManager = 6;

    public static final int AckManager = 7;

    public static final int AckBackup = 8;

    public static final int Transaction = 9;

    public static final int Repliche = 10;

    public static final int WorkflowResponse = 11;

    public static final int TransactionEnqueue = 12;

    public static final int TransactionDequeue = 13;

    public static final int IsManagerAlive = 14;

    public static final int QueueStatus = 15;
}
