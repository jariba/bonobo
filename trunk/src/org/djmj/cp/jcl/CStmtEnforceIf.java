/*
 * CStmtEnforceIf.java
 *
 * Created on August 16, 2004, 11:34 AM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Vector;
import org.djmj.cp.ce.CEConstraint;
import org.djmj.cp.ce.CECConstrainIf;

/**
 *
 * @author  Javier
 */
public class CStmtEnforceIf 
    implements CStmt 
{
    protected CStmt conditionStmt_;
    protected List constraintStmts_;
    
    public CStmtEnforceIf(CStmt cond,List constraints)     
    {
        conditionStmt_ = cond;
        constraintStmts_ = constraints;
    }
    
    public CEConstraint eval(JCLParserContext context) 
        throws Exception 
    {
        CEConstraint cond = conditionStmt_.eval(context);
        List constraints = new Vector();
        
        for (int i=0;i<constraintStmts_.size();i++) {
            CStmt constraintStmt = (CStmt)constraintStmts_.get(i);
            constraints.add(constraintStmt.eval(context));
        }
        
        return new CECConstrainIf(cond,constraints);
    }
    
    public void setViolationInfo(String vinfo) 
    {
        throw new RuntimeException("Can't set violation info on Enforce_If constraint");
    }    
}
