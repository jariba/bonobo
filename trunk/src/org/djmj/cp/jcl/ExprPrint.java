/*
 * ExprPrint.java
 *
 * Created on January 8, 2004, 12:45 AM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 * TODO: remove this when subroutine support is added to JCL
 * this will then be just a system function
 */
public class ExprPrint 
    implements Expr 
{
    protected Expr printExpr_;
    
    public ExprPrint(Expr printExpr) 
    {
        printExpr_ = printExpr;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        context.getOut().println(printExpr_.eval(context).toString());
        
        return VoidDataType.dataRef; 
    }
    
    public DataType getDataType() 
    {
        return VoidDataType.instance; 
    }    
}
