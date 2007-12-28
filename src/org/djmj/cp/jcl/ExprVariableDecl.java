/*
 * ExprVariableDecl.java
 *
 * Created on January 7, 2004, 11:38 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class ExprVariableDecl 
    implements Expr 
{
    protected String vname_;
    protected DataType vdt_;
    
    /** Creates a new instance of ExprVariableDecl */
    public ExprVariableDecl(String name, DataType dt) 
    {
        vname_ = name;
        vdt_ = dt;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception
    {
        Variable v = new Variable(vname_,vdt_);
        context.getSymbolTable().addVariable(v);
        
        return VoidDataType.dataRef; 
    }
    
    public DataType getDataType() 
    {
        return VoidDataType.instance; 
    }    
}
