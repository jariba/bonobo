/*
 * CENode.java
 *
 * Created on October 29, 2003, 6:07 PM
 */

package org.djmj.cp.ce;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;

/**
 *
 * @author  Javier
 */
public class CENode 
{
    private Object value_;
    private List constraints_;
    
    public CENode() 
    {
        value_ = null;
        constraints_ = new Vector();
    }    
    
    public Object getValue() { return value_; }
    
    public void setValue(Object o) 
    { 
        value_=o; 
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint)constraints_.get(i);
            c.nodeChanged(this);
        }
    }
    
    protected void addConstraint(CEConstraint c)
    {
        constraints_.add(c);
    }
    
    public List getConstraints()
    {
        return constraints_;
    }
    
    public String toString()
    {
        if (getValue()!=null)
            return getValue().toString();
        else
            return null;
    }
    
    /*
     * Puts all valid values for this node (taking into account active constraints)
     * into the list passed as arg.
     * TODO: For now this only works well for nodes with discrete domains and with 
     * CECMemberOf constraints
     * Need to make it consistent for all node types and all kinds of constraints.
     * (Implement bounds propagation)
     * for instance, if we have a double node with 2 constraints, n>3, n>5
     * the result of this would be the interval (2,5)- Need to be able to represent
     * continuous intervals.
     * result is passed as arg instead of returned to allow JCL to pass a KCL list
     */
    public void getDiscreteValidValues(CEList result)
    {
        Hashtable values = null;
        
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint)constraints_.get(i);
            if (c.isActive()) {
                if (c instanceof CECMemberOf) {
                    CECMemberOf c1 = (CECMemberOf)c;
                    values = mergeValidValues(values,c1.getValidValues());
                }
            }
        }                
        
        // TODO: create copies of the values moved to the result?
        // otherwise there could be problems in OSL
        if (values != null)
            result.addAll(values.keySet());
    }
        
    /*
     * This implements an intersection between oldValues and newValues
     */
    protected Hashtable mergeValidValues(Hashtable oldValues,CEList newValues)
    {       
        Hashtable retval = new Hashtable();
        if (oldValues==null) {
            for (int i=0;i<newValues.size();i++) 
                retval.put(newValues.get(i),newValues.get(i));
        }
        else {            
            for (int i=0;i<newValues.size();i++) {
                if (oldValues.get(newValues.get(i)) != null)
                    retval.put(newValues.get(i),newValues.get(i));
            }
        }
        
        return retval;
    }
        
}
