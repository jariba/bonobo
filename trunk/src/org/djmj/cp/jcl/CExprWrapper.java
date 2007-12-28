/*
 * CExprObjectFieldRef.java
 *
 * Created on January 8, 2004, 1:00 AM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CENode;

/**
 *
 * @author  Javier
 */
public class CExprWrapper 
    implements CExpr 
{
    Expr expr_;
    
    public CExprWrapper(Expr expr) 
    {
        expr_ = expr;
    }
    
    public CENode makeOutputNode(JCLParserContext context)
        throws Exception
    {
        Object o = expr_.eval(context).getStorage();
        if (o instanceof CENode)
            return (CENode)o;
        else {
            CENode retval = new CENode();   
            retval.setValue(o);
            return retval;
        }
    }   
    
    public DataType getDataType()
    {
        return expr_.getDataType();
    }
}
