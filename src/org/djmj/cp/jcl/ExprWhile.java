/*
 * ExprWhile.java
 *
 * Created on January 10, 2004, 5:20 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprWhile 
    implements Expr 
{
    protected Expr condExpr_;
    protected Expr bodyStmt_;
    
    public ExprWhile(Expr condExpr,Expr bodyStmt) 
    {
        condExpr_ = condExpr;
        bodyStmt_ = bodyStmt;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        DataRef cond;
        int condResult = 1;
        
        while (condResult != 0) {
            cond = condExpr_.eval(context);
            condResult = (new Double(cond.getValue().toString())).intValue();
            if (condResult != 0)
                bodyStmt_.eval(context);
        }
        
        return VoidDataType.dataRef; 
    }
    
    public DataType getDataType() 
    {
        return VoidDataType.instance; 
    }    
}
