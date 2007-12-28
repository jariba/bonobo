/*
 * CSumExpr.java
 *
 * Created on November 25, 2003, 12:22 AM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CENode;
import org.djmj.cp.ce.CECArith;

/**
 *
 * @author  Javier
 */
public class CExprArith 
    implements CExpr 
{
    protected String operator_;
    protected CExpr aExpr_;
    protected CExpr bExpr_;
    
    public CExprArith(String operator,CExpr aExpr, CExpr bExpr) 
    {
        operator_ = operator;
        aExpr_ = aExpr;
        bExpr_ = bExpr;
    }
     
    public CENode makeOutputNode(JCLParserContext context) 
        throws Exception
    {
        CENode outputNode = new CENode();
        new CECArith(
            operator_,
            aExpr_.makeOutputNode(context), 
            bExpr_.makeOutputNode(context),
            outputNode
        );
        
        return outputNode;
    }    
    
    public DataType getDataType() 
    {
        // TODO: think this through
        return aExpr_.getDataType(); 
    }    
}
