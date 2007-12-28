/*
 * CEConstraintBase.java
 *
 * Created on August 11, 2004, 4:09 PM
 */

package org.djmj.cp.ce;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author  Javier
 */
public abstract class CEConstraintBase
    implements CEConstraint
{
    protected double conflict_;
    protected boolean active_;
    protected String vinfo_;
    protected List nodes_;
    protected List observers_;
    
    public CEConstraintBase() 
    {
        nodes_ = new Vector();
        conflict_=NO_CONFLICT;
        active_=false;
        observers_ = new Vector();
    }
    
    public boolean isActive() { return active_; }
    
    public void activate() 
    { 
        active_=true; 
    }
    
    public void deactivate() 
    { 
        conflict_=NO_CONFLICT;
        active_=false; 
    }
    
    public double getConflict() { return conflict_; }
    /* 
     * violation info. TODO: make this more sophisticated later
     */
    public String getViolationInfo() { return vinfo_; }
    public void setViolationInfo(String s) { vinfo_ = s; }
    
    protected abstract void onNodeChanged(CENode node);

    public void nodeChanged(CENode node)
    {
        onNodeChanged(node);
        for (int i=0;i<observers_.size();i++) {
            CEConstraintObserver o = (CEConstraintObserver) observers_.get(i);
            o.onNodeChanged(this, node);
        }
    }
    
    protected void addNode(CENode n)
    {
        nodes_.add(n);
        n.addConstraint(this);
    }
    
    public String getConflictStr()
    {
        if (conflict_==NO_CONFLICT)
            return "NO_CONFLICT";
        
        if (conflict_==MAX_CONFLICT)
            return "MAX_CONFLICT";
        
        return Double.toString(conflict_);
    }
    
    // a convenience method until java generics are released
    protected CENode getNode(int i) { return (CENode)nodes_.get(i); }
    
    public void addObserver(CEConstraintObserver o)
    {
        observers_.add(o);
    }
    
    public void removeObserver(CEConstraintObserver o)
    {
        observers_.remove(o);
    }
    
    public boolean allNodesKnown()
    {
        for (int i=0;i<nodes_.size();i++) {
            CENode node = getNode(i);
            if (node.getValue()==null)
                return false;
        }
        
        return true;
    }
}
