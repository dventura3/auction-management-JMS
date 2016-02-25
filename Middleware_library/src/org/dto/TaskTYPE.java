/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;

/**
 * Classe che definisce un enumerativo permettendo di selezionare la tipologia di un Task.
 * @author Daniela88, LuigiXIV, Marcx87
 */
public final class TaskTYPE implements Serializable
{
    /**
     * Identificativo del tipo di Task.
     */
    private String id;
    /**
     * Costruttore privato.
     * @param id Identificativo del tipo.
     */
    private TaskTYPE(String  id)
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
    public static final TaskTYPE Executable = new TaskTYPE("Executable");
    /**
     * Attributo che definisce il tipo di task Script Bash.
     */
    public static final TaskTYPE scriptBash = new TaskTYPE("Script Bash");

}
