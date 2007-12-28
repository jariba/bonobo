/*
 * ExprStmtBlock.java
 *
 * Created on January 10, 2004, 5:12 PM
 */

package org.djmj.cp.jcl;

import java.util.List;

/**
 *
 * @author  Javier
 */
public class ExprStmtBlock 
    implements Expr 
{
    protected List stmts_;    
    
    public ExprStmtBlock(List stmts) 
    {
        stmts_ = stmts;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        for (int i=0; i<stmts_.size();i++) {
            Expr stmt = (Expr)stmts_.get(i);
            stmt.eval(context);
        }
        
        return VoidDataType.dataRef; 
    }
    
    public DataType getDataType() 
    {
        return VoidDataType.instance; 
    }    
}
