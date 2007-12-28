/*
 * CObject.java
 *
 * Created on November 23, 2003, 11:02 PM
 */

package org.djmj.cp.ce;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author  Javier
 */
public class CObject 
{
    boolean isConstrained_;
    protected List constraints_;
    
    public CObject() 
    {
        constraints_ = new Vector();
    }
    
    public List getConstraints() { return constraints_; }
    
    public void addConstraint(CEConstraint c)
    {
        constraints_.add(c);
    }
           
    public double getConflict()
    {
        if (!isConstrained_)
            return CEConstraint.NO_CONFLICT;
        
        double retval = 0.0;
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint) constraints_.get(i);
            retval += c.getConflict();
        }
        
        return retval;        
    }
    
    public String getViolations()
    {
        String retval = "";
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint) constraints_.get(i);
            if (c.getConflict() != CEConstraint.NO_CONFLICT)
                retval += c.getConflictStr()+" : "+c.getViolationInfo()+"\n";
        }
        
        return retval;
    }
        
    public void activateConstraints()
    {
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint) constraints_.get(i);
            c.activate();
        }
        isConstrained_ = true;
    }
    
    public void deactivateConstraints()
    {
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint) constraints_.get(i);
            c.deactivate();
        }
        isConstrained_ = false;
    }    
}
