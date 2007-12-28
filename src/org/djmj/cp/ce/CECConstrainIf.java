/*
 * CECConstrainIf.java
 *
 * Created on August 11, 2004, 4:31 PM
 */

package org.djmj.cp.ce;

import java.util.List;

/**
 *
 * @author  Javier
 *
 *  this is a constraint that is made p of a set of constraints which are activated/deactivated
 *  depending on whether another constraint (the condition) is satisified or not
 *  in JCL, for example :
 *  constrain_if(p.contract="GoldContract") {
 *      p.discount = 0.2 : "Discount for gold level is 20%"
 *      p.length*p.width >= 100 : "Dscount for GoldContract can only be applied to certain sizes"
 *  }
 */
public class CECConstrainIf 
    implements CEConstraint,CEConstraintObserver
{
    protected CEConstraint condition_;
    protected List constraints_;
    protected boolean active_;
    protected boolean activatedConstraints_;
    protected List observers_;
    
    public CECConstrainIf(CEConstraint condition,List constraints) 
    {
        condition_ = condition;
        constraints_ = constraints;
        active_ = false;
        activatedConstraints_ = false;
        condition.addObserver(this);
   }
    
    public boolean isActive() { return active_; }
    public List getConstraints() { return constraints_; }
    
    public void activate()    
    { 
        active_ = true; 
        condition_.activate();
        checkConstraint();
    }    
    
    public void deactivate()  
    { 
        condition_.deactivate();
        deactivateConstraints();
        active_= false; 
    }    
    
    public boolean isEnforced()
    {
        return active_ && activatedConstraints_;
    }
    
    public double getConflict() 
    {
        double conflict = NO_CONFLICT;
        if (isEnforced()) {
            for (int i=0;i<constraints_.size();i++) {
                CEConstraint c = (CEConstraint)constraints_.get(i);
                conflict += c.getConflict();
            }
        }
        
        return conflict;
    }
    
    public String getConflictStr()
    {
        double conflict = getConflict();
        
        if (conflict == NO_CONFLICT)
            return "NO_CONFLICT";
        
        if (conflict == MAX_CONFLICT)
            return "MAX_CONFLICT";
        
        return Double.toString(conflict);
    }    
    
    public String getViolationInfo() 
    {
        StringBuffer buf = new StringBuffer();
        
        if (isEnforced()) {
            for (int i=0;i<constraints_.size();i++) {
                CEConstraint c = (CEConstraint)constraints_.get(i);
                if (c.getConflict() > NO_CONFLICT)
                    buf.append(c.getViolationInfo()).append("\n");
            }
        }

        return buf.toString();
    }
    
    public void setViolationInfo(String s) 
    {
        throw new RuntimeException("Can't set violation info on ConstrainIf");
    }
        
    public void nodeChanged(CENode node) 
    {
        // This constrain doesn't have any nodes so this should never be called
        throw new RuntimeException("Can't call nodeChanged on ConstrainIf");        
    }
    
    public void addObserver(CEConstraintObserver o) 
    {
        observers_.add(o);
    }
    
    public void removeObserver(CEConstraintObserver o) 
    {
        observers_.remove(o);
    }    

    protected void checkConstraint()
    {
        /*
         * The constraint that represents the condition changed
         * Activate or deactivate contained constraints depending
         * on whether the condition is violated or not
         */
        if (condition_.getConflict()==NO_CONFLICT && condition_.allNodesKnown())
            activateConstraints();
        else
            deactivateConstraints();
    }
    
    /*
     * CEConstraintObserver interface
     */
    public void onNodeChanged(CEConstraint c, CENode n) 
    {
        if (active_)
            checkConstraint();
    }    
    
    protected void activateConstraints()
    {
        if (!activatedConstraints_) {
            for (int i=0;i<constraints_.size();i++) {
                CEConstraint c = (CEConstraint)constraints_.get(i);
                c.activate();
            }
            activatedConstraints_ = true;
        }
    }
    
    protected void deactivateConstraints()
    {
        if (activatedConstraints_) {
            for (int i=0;i<constraints_.size();i++) {
                CEConstraint c = (CEConstraint)constraints_.get(i);
                c.deactivate();
            }
            activatedConstraints_ = false;
        }
    }    
    
    public boolean allNodesKnown() 
    {
        return true;
    }    
}
