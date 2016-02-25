/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.XMLGen;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Classe che permette di mappare nel database xml il tipo complesso
 * TaskDescriptor
 * @author marcx87
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Task", propOrder = {
    "ID",
    "type",
    "command",
    "cpuRequired",
    "ramRequired",
    "spaceRequired",
    "operatingSystemRequired",
    "inputs",
    "outputs"
})
public class TaskItem {
    /**
     * L'identificativo del Task.
     */
    @XmlElement(required = true)
    protected int ID;
    /**
     * La tipologia del Task (Executable o Script Bash).
     */
    @XmlElement(required = true)
    protected String type;
    /**
     * Il comando da lanciare per eseguire il Task.
     */
    @XmlElement(required = true)
    protected String command;
    /**
     * Numero di CPU richieste per eseguire il Task.
     */
    @XmlElement(required = true)
    protected int cpuRequired;
    /**
     * Ram minima richiesta per eseguire il Task (espressa in MB).
     */
    @XmlElement(required = true)
    protected int ramRequired;
    /**
     * Spazio minimo su disco richiesto per eseguire il Task (espresso in MB).
     */
    @XmlElement(required = true)
    protected int spaceRequired;
    /**
     * Sistema operativo sul quale può eseguire il Task.
     */
    @XmlElement(required = true)
    protected String operatingSystemRequired;
    /**
     * Risorse fisiche ai file in input del Task.
     */
    protected ArrayList<String> inputs;
    /**
     * Risorse fisiche ai file in output del Task.
     */
    protected ArrayList<String> outputs;
     /**
     * Gets the value of the inputs property.
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputs property.
     * </p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputs().add(newItem);
     * </pre>
     *</p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     */
    public ArrayList<String> getInputs() {
         if (inputs == null) {
             inputs = new ArrayList<String>();
         }
         return this.inputs;
    }
    /**
     * Gets the value of the outputs property.
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outputs property.
     * </p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutputs().add(newItem);
     * </pre>
     *</p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     */
    public ArrayList<String> getOutputs() {
         if (outputs == null) {
             outputs = new ArrayList<String>();
         }
         return this.outputs;
    }
    /**
     * Gets the value of the queueID property.
     * @return
     *     possible object is
     *     {@link Integer }
     */
    public int getID() {
        return ID;
    }
    /**
     * Sets the value of the queueID property.
     * @param value
     *     allowed object is
     *     {@link Integer }
     */
    public void setID(int value) {
        this.ID = value;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String val){
        this.type = val;
    }

    public String getCommand(){
        return this.command;
    }

    public void setCommand(String val){
        this.command = val;
    }

    public int getCpuRequired(){
        return this.cpuRequired;
    }

    public void setCpuRequired(int val){
        this.cpuRequired = val;
    }

    public int getRamRequired(){
        return this.ramRequired;
    }

    public void setRamRequired(int val){
        this.ramRequired = val;
    }

    public int getSpaceRequired(){
        return this.spaceRequired;
    }

    public void setSpaceRequired(int val){
        this.spaceRequired = val;
    }

    public String getOperantingSystemRequired(){
        return this.operatingSystemRequired;
    }

    public void setOperatingSystemRequired(String val){
        this.operatingSystemRequired = val;
    }
}
