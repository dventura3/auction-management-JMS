/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;

/**
 * Classe che definisce un enumerativo permettendo di selezionare la tipologia di un OS.
 * @author marcx87
 */
public final class OSTYPE implements Serializable{
    /**
     * Identificativo del tipo di Task.
     */
    private String id;
    /**
     * Costruttore privato.
     * @param id Identificativo del tipo.
     */
    private OSTYPE(String  id)
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
     * Attributo che definisce il tipo di OS Windows XP.
     */
    public static final OSTYPE WindowsXP = new OSTYPE("Windows XP");
    /**
     * Attributo che definisce il tipo di OS Windows Vista.
     */
    public static final OSTYPE WindowsVista = new OSTYPE("Windows Vista");
    /**
     * Attributo che definisce il tipo di OS Windows 7.
     */
    public static final OSTYPE Windows7 = new OSTYPE("Windows 7");
    /**
     * Attributo che definisce il tipo di OS Unix based
     */
    public static final OSTYPE Unix = new OSTYPE("Unix based");
}
