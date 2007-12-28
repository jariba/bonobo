/*
 * ExprIf.java
 *
 * Created on January 10, 2004, 5:14 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprIf 
    implements Expr 
{
    protected Expr condExpr_;
    protected Expr ifStmt_;
    protected Expr elseStmt_;
    
    public ExprIf(Expr condExpr,Expr ifStmt,Expr elseStmt) 
    {
        condExpr_ = condExpr;
        ifStmt_ = ifStmt;
        elseStmt_ = elseStmt;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        DataRef cond = condExpr_.eval(context);
        int condResult = (new Double(cond.getValue().toString())).intValue();
        if (condResult != 0)
            ifStmt_.eval(context);
        else if (elseStmt_ != null)
            elseStmt_.eval(context);
        
        return VoidDataType.dataRef; 
    }
    
    public DataType getDataType() 
    {
        return VoidDataType.instance; 
    }    
}
