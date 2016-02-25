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
public class ActionTYPE implements Serializable{
     /**
     * Identificativo del tipo di Task.
     */
    private String id;
    /**
     * Costruttore privato.
     * @param id Identificativo del tipo.
     */
    private ActionTYPE(String  id)
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
    public static final ActionTYPE Status = new ActionTYPE("Status");
    /**
     * Attributo che definisce il tipo di task Script Bash.
     */
    public static final ActionTYPE EndAuction = new ActionTYPE("EndAuction");

    public static final ActionTYPE Bid = new ActionTYPE("Bid");
}
