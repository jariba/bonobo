/*
 * CECSum.java
 *
 * Created on November 25, 2003, 12:08 AM
 */

package org.djmj.cp.ce;

/**
 *
 * @author  Javier
 */
public class CECArith 
    extends CEConstraintBase
{    
    protected String operator_;
    
    /*
     * a <arith_op> b = c
     */
    public CECArith(String operator,CENode a, CENode b, CENode c) 
    {
        operator_ = operator;
        vinfo_ = operator_+" Constraint violated";
        addNode(a);
        addNode(b);        
        addNode(c);        
    }
    
    public void activate()
    {
        super.activate();
        checkConstraint();
    }
    
    protected void onNodeChanged(CENode node) 
    {
        // TODO: this is just to prevent an infinite loop for now,
        // need to fix CENode.setValue to prevent it from notifying the 
        // constraint that is setting the value
        if (node != getNode(2))
            checkConstraint();
    }    
    
    protected void checkConstraint()
    {
        Number aValue = (Number)getNode(0).getValue();
        Number bValue = (Number)getNode(1).getValue();
        Number cValue = (Number)getNode(2).getValue();
        
        if (aValue != null && bValue != null) {
            // TODO: just casting to the highest resolution right now
            // change to compute the type correctly
            double lhs = aValue.doubleValue();
            double rhs = bValue.doubleValue();
            
            double result = 0;
            if (operator_.equals("+"))
                result = lhs + rhs;
            else if (operator_.equals("-"))
                result = lhs - rhs;
            else if (operator_.equals("*"))
                result = lhs * rhs;
            else if (operator_.equals("/"))
                result = lhs / rhs;
            else if (operator_.equals("%"))
                result = lhs % rhs;
            
            getNode(2).setValue(new Double(result));
        }
    }
}
