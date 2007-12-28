/*
 * ExprMethodCall.java
 *
 * Created on January 11, 2004, 12:52 AM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author  Javier
 */
public class ExprMethodCall 
    implements Expr 
{
    protected String name_;
    protected List args_;
    protected DataType returnDT_;
    
    public ExprMethodCall(String name,List args,DataType returnDT) 
    {
        name_ = name;
        args_ = args;
        returnDT_ = returnDT;
    }
    
    public DataRef eval(JCLParserContext context) 
        throws Exception 
    {
        List argValues = new Vector();
        List argDTs = new Vector();
        
        for (int i=0; i< args_.size(); i++) {
            Expr arg = (Expr)args_.get(i);
            DataRef argValue = arg.eval(context);
            argValues.add(argValue);
            argDTs.add(argValue.getRuntimeDataType());
        }
        
        JCLMethod m = context.getSymbolTable().getBestMethodMatch(name_,argDTs);
        return m.eval(argValues,context); 
    }
    
    public DataType getDataType() 
    {
        return returnDT_;
    }    
}
