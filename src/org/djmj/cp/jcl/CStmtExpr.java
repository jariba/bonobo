/*
 * CEqualStmt.java
 *
 * Created on November 25, 2003, 12:02 AM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CEConstraint;
import org.djmj.cp.ce.CECRelational;
import org.djmj.cp.ce.CENode;

/**
 *
 * @author  Javier
 */
public class CStmtExpr 
    extends CStmtBase
{
    protected CExpr expr_;
    
    public CStmtExpr(CExpr expr) 
    {
        expr_ = expr;
    }
     
    protected CEConstraint makeConstraint(JCLParserContext context)
        throws Exception
    {
        CENode outputNode = expr_.makeOutputNode(context);
        return (CEConstraint)outputNode.getConstraints().get(0);
    }    
}
