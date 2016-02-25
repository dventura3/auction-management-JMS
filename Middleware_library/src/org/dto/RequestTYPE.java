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
public final class RequestTYPE implements Serializable{

    /**
     * Identificativo del tipo di Task.
     */
    private String id;
    /**
     * Costruttore privato.
     * @param id Identificativo del tipo.
     */
    private RequestTYPE(String  id)
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
     * Attributo che definisce il tipo di task Executable.
     */
    public static final RequestTYPE QueueStatus = new RequestTYPE("QueueStatus");
    /**
     * Attributo che definisce il tipo di task Script Bash.
     */
    public static final RequestTYPE Enqueue = new RequestTYPE("Enqueue");
    /**
     * 
     */
    public static final RequestTYPE Dequeue = new RequestTYPE("Dequeue");

}
