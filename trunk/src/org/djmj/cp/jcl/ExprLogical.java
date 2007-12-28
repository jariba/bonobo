/*
 * ExprLogical.java
 *
 * Created on January 9, 2004, 11:27 PM
 */

package org.djmj.cp.jcl;

import java.util.List;

/**
 *
 * @author  Javier
 */
public class ExprLogical 
    implements Expr 
{
    protected String operator_;
    protected List children_;
    
    public ExprLogical(String operator,List children) 
    {
        operator_ = operator;
        children_ = children;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        boolean result;
        if (operator_.equals("&&"))
            result = true;
        else  // ||
            result = false;
        
        for (int i=0;i<children_.size();i++) {
            Expr child = (Expr)children_.get(i);
            int value = ((Number)child.eval(context).getStorage()).intValue();
            
            if (value==0) { // child is false
                if (operator_.equals("&&")) {
                    result = false;
                    break;
                }
            }
            else { // child is true
                if (operator_.equals("||")) {
                    result = true;
                    break;
                }                
            }
        }
        
        return new DataRef(PrimitiveDataType.INTEGER,new Integer(result ? 1 : 0));
    }
    
    public DataType getDataType() 
    {
        return PrimitiveDataType.INTEGER;
    }    
}
