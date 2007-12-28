/*
 * CEqual.java
 *
 * Created on November 12, 2003, 3:58 PM
 */

package org.djmj.cp.ce;

import java.util.List;

/**
 *
 * @author  Javier
 * Logical AND (&&) and OR (||) constraints
 *
 */
public class CECLogical 
    extends CEConstraintBase 
{
    protected String operator_;
    protected CENode outputNode_;
    
    /*
     * a <relop> b
     */
    public CECLogical(String operator, List nodes, CENode outputNode) 
    { 
        operator_ = operator;
        vinfo_ = operator_+" Constraint violated";
        for (int i=0; i<nodes.size();i++)
            addNode((CENode)nodes.get(i));
        addNode(outputNode);        
        outputNode_ = outputNode;
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
        if (node != outputNode_)
            checkConstraint();
    }    
    
    protected void checkConstraint()
    {
        // TODO: keep track of nodes that are null so this isn't computed every time
        for (int i=0;i<nodes_.size()-1;i++) {
            Boolean value = (Boolean)getNode(i).getValue();
            if (value==null) {
                setNewConflict(NO_CONFLICT);
                return;
            }
            else if (operator_.equals("||") && value.booleanValue()) {
                setNewConflict(NO_CONFLICT);
                return;
            }
            else if (operator_.equals("&&") && !value.booleanValue()) {
                setNewConflict(MAX_CONFLICT);
                return;
            }
        }
        
        if (operator_.equals("||"))
            setNewConflict(MAX_CONFLICT);
        else // operator_.equals("&&")
            setNewConflict(NO_CONFLICT);
    }
    
    protected void setNewConflict(double newConflict)
    {
          conflict_ = newConflict;
          if (conflict_==NO_CONFLICT)
              outputNode_.setValue(Boolean.TRUE);
          else
              outputNode_.setValue(Boolean.FALSE);
    }    
}
