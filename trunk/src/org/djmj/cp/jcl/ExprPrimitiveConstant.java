/*
 * ExprVariableRef.java
 *
 * Created on November 21, 2003, 11:35 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprPrimitiveConstant 
    implements Expr 
{
    DataRef ref_;
    
    public ExprPrimitiveConstant(DataType dt,Object storage) 
    {
        ref_ = new DataRef(dt,storage);
    }

    public DataRef eval(JCLParserContext context) 
        throws Exception
    {
        return ref_;
    }
    
    public DataType getDataType() 
    {
        return ref_.getDataType();
    }    
}
