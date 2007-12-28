/*
 * Variable.java
 *
 * Created on November 21, 2003, 4:58 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class Variable 
{
    protected String name_;
    protected DataRef ref_;
    
    public Variable(String name,DataType dt) 
    {
        name_ = name;
        ref_ = new DataRef(dt);
    }

    /*
     * This is used by method calls to pass parameter values quickly
     * See UserMethod class
     * This means args are passed by reference, not by value
     */
    public Variable(String name,DataRef ref) 
    {
        name_ = name;
        ref_ = ref;
    }
    
    public String getName() { return name_; }
    public DataRef getDataRef() { return ref_; }
    public DataType getDataType() { return ref_.getDataType(); }
}
