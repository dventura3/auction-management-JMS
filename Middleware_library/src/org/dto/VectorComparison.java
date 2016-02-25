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
public class VectorComparison implements Serializable{
    private String id;

    private VectorComparison(String  id)
    {
        this.id = id;
    }

     @Override
    public String toString()
    {
        return this.id;
    }

    public static final VectorComparison EQUAL = new VectorComparison("EQUAL");
    public static final VectorComparison GREATER = new VectorComparison("GREATER");
    public static final VectorComparison SMALLER = new VectorComparison("SMALLER");
    public static final VectorComparison SIMULTANEOUS = new VectorComparison("SIMULTANEOUS");

}
