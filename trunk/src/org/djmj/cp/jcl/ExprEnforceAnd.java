/*
 * ExprEnforceAnd.java
 *
 * Created on January 7, 2004, 10:28 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import org.djmj.cp.ce.CObject;

/**
 *
 * @author  Javier
 */
public class ExprEnforceAnd 
    implements Expr 
{
    protected Expr varExpr_;
    protected List constraintStmts_;
    
    public ExprEnforceAnd(Expr varExpr,List constraintStmts) 
    {
        varExpr_ = varExpr;
        constraintStmts_ = constraintStmts;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception
    {
        DataRef ref = varExpr_.eval(context);
        CObject cobj = (CObject)((JavaClassStorage)ref.getStorage()).getJavaObject();
        for (int i=0; i<constraintStmts_.size();i++) {
            CStmt cstmt = (CStmt)constraintStmts_.get(i);
            cobj.addConstraint(cstmt.eval(context));
        }
        
        return VoidDataType.dataRef; 
    }
    
    public DataType getDataType() 
    {
        return VoidDataType.instance; 
    }    
}
