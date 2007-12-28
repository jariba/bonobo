/*
 * CStmt.java
 *
 * Created on November 24, 2003, 11:59 PM
 */

package org.djmj.cp.jcl;


import org.djmj.cp.ce.CEConstraint;

/**
 *
 * @author  Javier
 */
public interface CStmt 
{
    public CEConstraint eval(JCLParserContext context) throws Exception;
    public void setViolationInfo(String vinfo);
}
