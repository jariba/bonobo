/*
 * CExpr.java
 *
 * Created on November 25, 2003, 12:01 AM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CENode;

/**
 *
 * @author  Javier
 */
public interface CExpr 
{
    public DataType getDataType();
    public CENode makeOutputNode(JCLParserContext context) throws Exception;    
}
