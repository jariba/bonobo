/*
 * ExprObjectFieldRef.java
 *
 * Created on January 8, 2004, 12:03 AM
 */

package org.djmj.cp.jcl;

import java.util.List;

/**
 *
 * @author  Javier
 */
public class ExprObjectFieldRef 
    implements Expr 
{
    protected Expr varExpr_;
    protected List fieldNames_;
    protected DataType dt_;
    
    public ExprObjectFieldRef(Expr varExpr,List fieldNames,DataType dt) 
    {
        varExpr_ = varExpr;
        fieldNames_ = fieldNames;
        dt_ = dt;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        DataRef ref = varExpr_.eval(context);
        for (int i=0; i<fieldNames_.size();i++) {
            if (ref.getStorage()==null)
                throw new Exception("Can't access field of a null object");
            
            DataType dt = ref.getDataType();
            ref = dt.getMemberValue(ref.getStorage(),fieldNames_.get(i).toString());            
        }
        
        return ref;
    }
    
    public DataType getDataType() 
    {
        return dt_;
    }
    
}
