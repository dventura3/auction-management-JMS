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
 * Implementazione di un task.
 * @author Daniela88, LuigiXIV, Marcx87
 */

public class TaskDescriptor implements Serializable
{
    /**
     * L'identificativo del Task.
     */
    private int ID;
    /**
     * La tipologia del Task (Executable o Script Bash).
     * @see TaskTYPE
     */
    private TaskTYPE type;
    /**
     * Il comando da lanciare per eseguire il Task.
     */
    private String command;
    /**
     * Numero di CPU richieste per eseguire il Task.
     */
    private int cpuRequired;
    /**
     * Ram minima richiesta per eseguire il Task (espressa in MB).
     */
    private int ramRequired;
    /**
     * Spazio minimo su disco richiesto per eseguire il Task (espresso in MB).
     */
    private int spaceRequired;
    /**
     * Sistema operativo sul quale può eseguire il Task.
     */
    private OSTYPE operatingSystemRequired;
    /**
     * Risorse fisiche ai file in input del Task.
     */
    private ArrayList<String> inputs;
    /**
     * Risorse fisiche ai file in output del Task.
     */
    private ArrayList<String> outputs;
    /**
     * Costruttore di default.
     */
    public TaskDescriptor()
    {
        UUID id = UUID.randomUUID();
        this.ID = (int) id.getMostSignificantBits();
    }
    /**
     * Metodo per ottenere l'identificativo di un Task Descriptor.
     * @return Identificativo del Task Descriptor.
     */
    public int getID()
    {
       return this.ID;
    }
    /**
     *  Metodo per settare l'identificativo del Task Descriptor.
     * @param val Nuovo identificativo del Task Descriptor.
     */
    public void setID(int val)
    {
        this.ID = val;
    }
    /**
     * Metodo per ottenere la tipologia del Task.
     * @see TaskTYPE
     * @return Tipologia del task (Executable o Script Bash).
     */
    public TaskTYPE getType()
    {
        return this.type;
    }
    /**
     * Metodo per settare la tipologia di un Task.
     * @see TaskTYPE
     * @param val Tipologia del task (Executable o Script Bash).
     */
    public void setType(TaskTYPE val)
    {
        this.type = val;
    }
    /**
     * Metodo per ottenere i link alle risorse fisiche del Task.
     * @return Il link alla risorsa fisica del Task.
     */
    public ArrayList<String> getInputs()
    {
        return this.inputs;
    }
    /**
     *Metodo per settare i link alle risorse fisiche del Task.
     * @param val Nuovo link alla risorsa fisica del Task.
     */
    public void setInputs(Collection<String> val)
    {
        this.inputs = (ArrayList<String>) val;
    }
    /**
     *Metodo per ottenere il comando per eseguire il Task.
     * @return Il comando per eseguire il Task.
     */
    public String getCommand()
    {
        return this.command;
    }
    /**
     *Metodo per settare un nuovo comando per eseguire il Task.
     * @param val Il nuovo comando per eseguire il Task.
     */
    public void setCommand(String val)
    {
        this.command = val;
    }
    /**
     *Metodo per ottenere i link alle risorse fisiche in cui sono contunuti i risultati del Task.
     * @return I link alle risorse fisiche in cui sono contunuti i risultati del Task.
     */
    public ArrayList<String> getOutputs()
    {
        return this.outputs;
    }
    /**
     *Metodo per settare i link alle risorse fisiche in cui sono contunuti i risultati del Task.
     * @param val I nuovi link alle risorse fisiche in cui sono contunuti i risultati del Task.
     */
    public void setOutputs(Collection<String> val)
    {
        this.outputs = (ArrayList<String>) val;
    }
    /**
     *Metodo per ottenere il numero di cpu richieste per eseguire il Task.
     * @return Il numero di cpu richieste per eseguire il Task.
     */
    public int getCpuRequired()
    {
        return this.cpuRequired;
    }
    /**
     *Metodo per settare il numero di cpu richieste per eseguire il Task.
     * @param val Il nuovo numero di cpu richieste per eseguire il Task.
     */
    public void setCpuRequired(int val)
    {
        this.cpuRequired = val;
    }
    /**
     *Metodo per ottenere la quantità di ram necessaria per eseguire il Task.
     * @return La quantità di ram necessaria per eseguire il Task.
     */
    public int getRamRequired()
    {
        return this.ramRequired;
    }
    /**
     *Metodo per settare la quantità di ram necessaria per eseguire il Task.
     * @param val La nuova quantità di ram necessaria per eseguire il Task.
     */
    public void setRamRequired(int val)
    {
        this.ramRequired = val;
    }
    /**
     *Metodo per ottenere lo spazio su disco necessario per eseguire il Task.
     * @return Lo spazio su disco necessario per eseguire il Task.
     */
    public int getSpaceRequired()
    {
        return this.spaceRequired;
    }
    /**
     *Metodo per settare  lo spazio su disco necessario per eseguire il Task.
     * @param val  Il nuovo spazio su disco necessario per eseguire il Task.
     */
    public void setSpaceRequired(int val)
    {
        this.spaceRequired = val;
    }
    /**
     *Metodo per ottenere il sistema operativo necessario per eseguire il Task.
     * @return Il sistema operativo necessario per eseguire il Task.
     */
    public OSTYPE getOperatingSystemRequired()
    {
        return this.operatingSystemRequired;
    }
    /**
     *Metodo per settare il sistema operativo necessario per eseguire il Task.
     * @param val Il nuovo sistema operativo necessario per eseguire il Task.
     */
    public void setOperantingSystemRequired(OSTYPE val)
    {
        this.operatingSystemRequired = val;
    }
    /**
     *Metodo che ritorna la descrizione del Task
     * @return Descrizione del Task
     */
    @Override
    public String toString()
    {
        StringBuilder taskString = new StringBuilder(
                "///\n" + "TaskID: " + this.ID + "\n"
                + "TaskType: " +this.type + "\n"
                + "TaskCommand: " +this.command + "\n"
                + "TaskCpuRequired: " +this.cpuRequired + "\n"
                + "TaskRamRequired: " +this.ramRequired +"\n"
                + "TaskSpaceRequired: " +this.spaceRequired + "\n"
                + "TaskOpeartingSystemRequired: " +this.operatingSystemRequired + "\n"
                + "TaskInputs: ");
        Iterator<String> it = inputs.iterator();
        while(it.hasNext())
        {
            taskString.append(it.next());
            taskString.append("; ");
        }
        taskString.append("\nTaskOutputs: ");
        it = outputs.iterator();
        while(it.hasNext())
        {
            taskString.append(it.next());
            taskString.append("; ");
        }
        taskString.append("\n");
        return taskString.toString();
    }

}
