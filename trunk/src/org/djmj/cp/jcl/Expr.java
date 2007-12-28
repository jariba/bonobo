/*
 * Expr.java
 *
 * Created on November 21, 2003, 11:33 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public interface Expr 
{
    public DataType getDataType();
    public DataRef eval(JCLParserContext context) throws Exception;
}
