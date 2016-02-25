/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

import java.io.Serializable;

/**
 *
 * @author marcx87
 */
public final class TwoPCState implements Serializable{
    /**
     * Identificativo del tipo di Task.
     */
    private String id;
    /**
     * Costruttore privato.
     * @param id Identificativo del tipo.
     */
    private TwoPCState(String  id)
    {
        this.id = id;
    }

    /**
     *  Metodo che permette di stampare a schermo il nome del tipo del Task.
     * @return Il nome del tipo.
     */
    @Override
    public String toString()
    {
        return this.id;
    }

    /**
     * Attributo che definisce lo stato di prepare da parte di un TM.
     */
    public static final TwoPCState Prepare = new TwoPCState("PREPARE");
    /**
     * Attributo che definisce lo stato di ready da parte di un RM.
     */
    public static final TwoPCState Ready = new TwoPCState("READY");
    /**
     * Attributo che definisce lo stato di not ready da parte di un RM.
     */
    public static final TwoPCState NotReady = new TwoPCState("NOT_READY");
    /**
     * Attributo che definisce lo stato di global commit da parte di un TM.
     */
    public static final TwoPCState Commit = new TwoPCState("GLOBAL_COMMIT");
    /**
     * Attributo che definisce lo stato di global abort da parte di un TM.
     */
    public static final TwoPCState Abort = new TwoPCState("GLOBAL_ABORT");
    /**
     * Attributo che definisce lo stato di complete da parte di un TM.
     */
    public static final TwoPCState Complete = new TwoPCState("COMPLETE");

}
