/*
 * ExprNewObject.java
 *
 * Created on January 8, 2004, 12:34 AM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprNewObject 
    implements Expr 
{
    protected DataType dt_;
    
    public ExprNewObject(DataType dt) 
    {
        dt_ = dt;
    }
    
    public DataRef eval(JCLParserContext context)
        throws Exception 
    {
        Object newObject = dt_.newInstance();
        return new DataRef(dt_,newObject);        
    }
    
    public DataType getDataType() 
    {
        return dt_;
    }    
}
