/*
 * UserMethod.java
 *
 * Created on January 11, 2004, 11:06 AM
 */

package org.djmj.cp.jcl;

import java.util.List;

/**
 *
 * @author  Javier
 */
public class UserMethod 
    extends JCLMethod 
{
    protected List parameterNames_;
    protected Expr bodyExpr_;
    
    public UserMethod(String name, 
                      List parameterNames,
                      List parameterDTs, 
                      DataType returnDT,
                      Expr bodyExpr) 
    {
        super(name, parameterDTs, returnDT);
        parameterNames_ = parameterNames;
        bodyExpr_ = bodyExpr;
    }
    
    public DataRef eval(List args, JCLParserContext context) 
        throws Exception 
    {
        JCLSymbolTable methodST = new JCLSymbolTable(context.getSymbolTable());
        // Add return value as var
        Variable returnVar = new Variable(name_,returnDT_);
        methodST.addVariable(returnVar);
        // Add parameters as vars
        for (int i=0;i<parameterNames_.size();i++) {
            methodST.addVariable(
                new Variable(
                    (String)parameterNames_.get(i),
                    (DataRef)args.get(i)
                )
            );
        }
        
        context.setSymbolTable(methodST);
        bodyExpr_.eval(context);
        context.setSymbolTable(methodST.getParent());
        
        return returnVar.getDataRef();
    }    
}
