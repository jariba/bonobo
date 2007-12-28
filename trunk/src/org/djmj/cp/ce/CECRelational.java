/*
 * CEqual.java
 *
 * Created on November 12, 2003, 3:58 PM
 */

package org.djmj.cp.ce;

/**
 *
 * @author  Javier
 */
public class CECRelational 
    extends CEConstraintBase 
{
    protected String operator_;
    
    /*
     * a <relop> b
     */
    public CECRelational(String operator,CENode a, CENode b, CENode c) 
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
        Object aValue = getNode(0).getValue();
        Object bValue = getNode(1).getValue();

        if (aValue != null && bValue != null) {
            Comparable lhs = safeCast(aValue);
            Comparable rhs = safeCast(bValue);
            int comparison = lhs.compareTo(rhs);
            boolean result = true;

            if (operator_.equals("=="))
                result = (comparison==0);
            else if (operator_.equals("!="))
                result = (comparison!=0);
            else if (operator_.equals(">"))
                result = (comparison>0);
            else if (operator_.equals(">="))
                result = (comparison>=0);
            else if (operator_.equals("<"))
                result = (comparison<0);
            else if (operator_.equals("<="))
                result = (comparison<=0);
        
            getNode(2).setValue(new Boolean(result));
            if (!result) {
               conflict_ = MAX_CONFLICT; 
               return;
            }   
        }
        
        conflict_ = NO_CONFLICT;
    }
    
    protected Comparable safeCast(Object o)
    {
        if (o instanceof Number) 
            return new Double(((Number)o).doubleValue());
        
        return (Comparable)o;
    }    
}
