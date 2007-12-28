/*
 * ExprArith.java
 *
 * Created on January 10, 2004, 12:02 AM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprArith 
    implements Expr 
{
    protected String operator_;
    protected Expr lhs_;
    protected Expr rhs_;
    
    public ExprArith(String operator,Expr lhs,Expr rhs) 
    {
        operator_ = operator;
        lhs_ = lhs;
        rhs_ = rhs;        
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        Number lhsValue = (Number)lhs_.eval(context).getValue();
        Number rhsValue = (Number)rhs_.eval(context).getValue();
        
        // TODO: add support for null DataRefs?, should return null here
        if (lhsValue==null) lhsValue = new Double(0);
        if (rhsValue==null) rhsValue = new Double(0);
        
        // TODO: just casting to the highest resolution right now
        // change to compute the type correctly
        double lhs = lhsValue.doubleValue();
        double rhs = rhsValue.doubleValue();
        double result = 0;
        
        if (operator_.equals("+"))
            result = lhs + rhs;
        else if (operator_.equals("-"))
            result = lhs - rhs;
        else if (operator_.equals("*"))
            result = lhs * rhs;
        else if (operator_.equals("/"))
            result = lhs / rhs;
        else if (operator_.equals("%"))
            result = lhs % rhs;
        
        return new DataRef(PrimitiveDataType.DOUBLE, new Double(result));
    }
    
    public DataType getDataType() 
    {
        // TODO: just casting to the highest resolution right now
        // change to compute the type correctly
        return PrimitiveDataType.DOUBLE;
    }    
}
