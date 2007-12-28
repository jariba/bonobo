/*
 * CECMemberOf.java
 *
 * Created on August 4, 2004, 11:52 AM
 */

package org.djmj.cp.ce;

/**
 *
 * @author  Javier
 * This constraints takes 2 args :
 * CENode n
 * CEList l
 * it makes sure the value for n is one of the values in l
 * otherwise the constraint is in violation
 */
public class CECMemberOf 
    extends CEConstraintBase 
{    
    protected CEList validValues_;
    
    public CECMemberOf(CENode attr, CEList values) 
    {
        addNode(attr);
        validValues_=values;
    }
    
    public void activate()
    {
        super.activate();
        checkConstraint();
    }
    
    protected void onNodeChanged(CENode node) 
    {
        checkConstraint();
    }

    protected void checkConstraint()
    {
        Object attrValue = getNode(0).getValue(); 
        // TODO: should null values be ignored?
        if (attrValue==null || validValues_.containsValue(attrValue))
            conflict_ = NO_CONFLICT;        
        else
            conflict_ = MAX_CONFLICT;        
    }
    
    public CEList getValidValues()
    {
        return validValues_;
    }
}
