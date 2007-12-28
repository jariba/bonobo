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
public class ExprVariableRef 
    implements Expr 
{
    protected String vname_;
    protected DataType dt_;
    
    public ExprVariableRef(String vname,DataType dt) 
    {
        vname_ = vname;
        dt_ = dt;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception
    {
        Variable v = context.getSymbolTable().getVariable(vname_);
        return v.getDataRef();
    }
    
    public DataType getDataType() 
    {
        return dt_;
    }    
}
