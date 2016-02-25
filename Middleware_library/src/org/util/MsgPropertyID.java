/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

/**
 *
 * @author marcx87
 */
public final class MsgPropertyID {
    /**
     * Attributo che definisce lo stato di prepare da parte di un TM.
     */
    public static final String Type = "TYPE";
    /**
     * Attributo che definisce lo stato di ready da parte di un RM.
     */
    public static final String Source = "SOURCE";
    /**
     * Attributo che definisce lo stato di not ready da parte di un RM.
     */
    public static final String Destination = "DESTINATION";
    /**
     * Attributo che definisce lo stato di global commit da parte di un TM.
     */
    public static final String CoorMessage = "COORMESSAGE";

    public static final String TransMessage = "TRANSMESSAGE";
}
