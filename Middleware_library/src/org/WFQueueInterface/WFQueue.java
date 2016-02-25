/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.WFQueueInterface;

import org.dto.Workflow;

/**
 * Interfaccia della coda contenente i Workflow provenienti dalla UI.
 * @author Daniela88, LuigiXIV, Marcx87
 */
public interface WFQueue
{
    /**
     * Metodo per aggiungere un Workflow in coda.
     * @param wf Il workflow da inserire in coda.
     * @return La posizione dell'elemento in coda o un valore minore di zero in caso di errore.
     */
    public int enqueue(Workflow wf);
     /**
     * Metodo per estrarre l'ultimo Workflow presente in coda.
     * @return Il primo elemento in coda, o null se vuota.
     */
    public Workflow dequeue();
}
