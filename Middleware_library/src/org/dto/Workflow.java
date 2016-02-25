/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

/**
 * Implementazione di un workflow contenente un insieme di Task.
 * @author Daniela88, LuigiXIV, Marcx87
 */

public class Workflow implements Serializable
{
    /**
     * Struttura dati contenente i Task del Workflow e i file per eseguirli.
     */
    private ArrayList<TaskDescriptor> tasks;
    /**
     * Identificativo del Workflow.
     */
    private int ID;
    /**
     * Costruttore di default.
     */
    public Workflow()
    {
        UUID id = UUID.randomUUID();
        this.ID = (int) id.getMostSignificantBits();
        this.tasks = new ArrayList<TaskDescriptor>();
    }
    /**
     *  Metodo che ritorna i Task Descriptor presenti nel Workflow.
     * @see TaskDescriptor TaskDescriptor.
     * @return I Task nel Workflow.
     */
    public Collection<TaskDescriptor> getTasks()
    {
        return this.tasks;
    }

    /**
     * Metodo che ritorna il Task Descriptor avente una determinata posizione se presente nel Workflow, null altrimenti.
     * @see TaskDescriptor TaskDescriptor.
     * @param index Posizione del Task nel Workflow.
     * @return Il Task Descriptor associato all'identificativo o null se non vi è  tale Task nel Workflow.
     */
    public TaskDescriptor getTask(int index)
    {
        if(index > this.tasks.size() || index <0)
        {
            return null;
        }
        TaskDescriptor td = this.tasks.get(index);
        return td;
    }
    /**
     * Metodo per settare i task di un Workflow.
     * @see TaskDescriptor TaskDescriptor.
     * @param val Vettore di Task Descriptor.
     */
    public void setTasks(Collection<TaskDescriptor> val)
    {
          ArrayList<TaskDescriptor> vect=(ArrayList<TaskDescriptor>)  val;
          this.tasks.addAll(vect);
    }
    /**
     * Metodo per aggiungere un singolo Task nel Workflow.
     * @see TaskDescriptor TaskDescriptor.
     * @param ts Il Task Descriptor da aggiungere al Wofkflow.
     * @return L'indice del Task nel Workflow.
     */
    public int addTask(TaskDescriptor ts) 
    {
        this.tasks.add(ts);
        return this.tasks.size();
    }
    /**
     * Metodo per ottenere l'identificativo del Workflow.
     * @return Identificativo del Workflow.
     */
    public int getID()
    {
        return this.ID;
    }
    /**
     * Metodo per settare un nuovo identificativo.
     * @param val Nuovo identificativo del Workflow.
     */
    public void setID(int val)
    {
        this.ID = val;
    }
    /**
     * Metodo che ritorna la descrizione di un Workflow sotto forma di stringa.
     * @return Workflow sotto forma di stringa.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        Iterator itr = this.tasks.iterator();
        while(itr.hasNext())
        {
            TaskDescriptor element = (TaskDescriptor) itr.next();
            sb.append(element.toString());
        }
        return "#----------------\nWorkFlowID: " +this.ID + "\n" + "//////\n"
                + "Workflow Tasks:\n" + sb.toString() + "#----------------";
    }
}
