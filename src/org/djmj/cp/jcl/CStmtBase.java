/*
 * CStmtBase.java
 *
 * Created on August 5, 2004, 4:40 PM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CEConstraint;

/**
 *
 * @author  Javier
 */
public abstract class CStmtBase 
    implements CStmt 
{
    protected String vinfo_;
    
    public CStmtBase() 
    {
    }
    
    public CEConstraint eval(JCLParserContext context) 
        throws Exception 
    {
         CEConstraint c = makeConstraint(context);        
         c.setViolationInfo(vinfo_);
         
         return c;
    }
    
    public void setViolationInfo(String vinfo) 
    {
        vinfo_ = vinfo;
    }
        
    protected abstract CEConstraint makeConstraint(JCLParserContext context)
        throws Exception;
}
