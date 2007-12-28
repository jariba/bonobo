/*
 * JCLClient.java
 *
 * Created on December 15, 2003, 10:19 AM
 */

package org.djmj.cp.jcl.client;

/**
 *
 * @author  Javier
 */
public interface JCLClient 
{
    public void evalProgram(String kclProgram)
        throws Exception;    
    
    public Object evalExpr(String kclExpr) 
        throws Exception;
    
    public String getJCLLiteral(String kclType,Object value);    
}
