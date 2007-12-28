/*
 * ExprAssignment.java
 *
 * Created on January 7, 2004, 11:49 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprAssignment 
    implements Expr 
{
    protected Expr lhs_;
    protected Expr rhs_;
    
    public ExprAssignment(Expr lhs,Expr rhs) 
    {
        lhs_ = lhs;
        rhs_ = rhs;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception
    {
        DataRef ref = lhs_.eval(context);
        ref.assign(rhs_.eval(context));
        
        return ref;
    }
    
    public DataType getDataType() 
    {
        return lhs_.getDataType();
    }    
}
