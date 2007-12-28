/*
 * ExprRelational.java
 *
 * Created on January 9, 2004, 11:45 PM
 */

package org.djmj.cp.jcl;

import java.lang.Comparable;

/**
 *
 * @author  Javier
 */
public class ExprRelational 
    implements Expr 
{
    protected String operator_;
    protected Expr lhs_;
    protected Expr rhs_;
    
    public ExprRelational(String operator,Expr lhs,Expr rhs) 
    {
        operator_ = operator;
        lhs_ = lhs;
        rhs_ = rhs;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        Object lhsValue = lhs_.eval(context).getValue();
        Object rhsValue = rhs_.eval(context).getValue();
        
        // Any comparison with null will eval to false
        if (lhsValue==null || rhsValue==null) 
            return new DataRef(PrimitiveDataType.INTEGER,new Integer(0));
        
        Comparable lhs = safeCast(lhsValue);
        Comparable rhs = safeCast(rhsValue);
        int comparison = lhs.compareTo(rhs);
        boolean result = true;
        
        if (operator_.equals("=="))
            result = (comparison==0);
        else if (operator_.equals("!="))
            result = (comparison!=0);
        else if (operator_.equals(">"))
            result = (comparison>0);
        else if (operator_.equals(">="))
            result = (comparison>=0);
        else if (operator_.equals("<"))
            result = (comparison<0);
        else if (operator_.equals("<="))
            result = (comparison<=0);
        
        return new DataRef(PrimitiveDataType.INTEGER,new Integer(result ? 1 : 0));
    }
    
    public DataType getDataType() 
    {
        return PrimitiveDataType.INTEGER;
    }    
    
    protected Comparable safeCast(Object o)
    {
        if (o instanceof Number) 
            return new Double(((Number)o).doubleValue());
        
        return (Comparable)o;
    }
}
