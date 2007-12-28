/*
 * ExprDataRef.java
 *
 * Created on November 23, 2003, 1:38 AM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprDataRef 
    implements Expr 
{
    // TODO: deal with exprs that only have a lval or an rval
    protected DataRef ref_;
    
    public ExprDataRef(DataRef ref) 
    {
        ref_ = ref;
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
