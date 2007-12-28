/*
 * CExprRelational.java
 *
 * Created on August 27, 2004, 4:55 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Vector;
import org.djmj.cp.ce.CENode;
import org.djmj.cp.ce.CECLogical;

/**
 *
 * @author  Javier
 */
public class CExprLogical
    implements CExpr 
{
    protected String operator_;
    protected List children_;
    
    /** Creates a new instance of CExprRelational */
    public CExprLogical(String operator, List children) 
    {
        operator_ = operator;
        children_ = children;
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
        List nodes = new Vector();
        for (int i=0;i<children_.size();i++) {
            nodes.add(((CExpr)children_.get(i)).makeOutputNode(context));
        }
        
        new CECLogical(
            operator_,
            nodes, 
            outputNode
        );
        
        return outputNode;
    }    
}
