/*
 * CExprRelational.java
 *
 * Created on August 27, 2004, 4:55 PM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CENode;
import org.djmj.cp.ce.CECRelational;

/**
 *
 * @author  Javier
 */
public class CExprRelational 
    implements CExpr 
{
    protected String operator_;
    protected CExpr lhs_;
    protected CExpr rhs_;
    
    /** Creates a new instance of CExprRelational */
    public CExprRelational(String operator,CExpr lhs, CExpr rhs) 
    {
        operator_ = operator;
        lhs_ = lhs;
        rhs_ = rhs;
    }
    
    public DataType getDataType() 
    {
        // TODO: think this through
        return PrimitiveDataType.INTEGER;
    }
    
    public CENode makeOutputNode(JCLParserContext context) 
        throws Exception 
    {
        CENode outputNode = new CENode();
        new CECRelational(
            operator_,
            lhs_.makeOutputNode(context), 
            rhs_.makeOutputNode(context),
            outputNode
        );
        
        return outputNode;
    }
    
}
