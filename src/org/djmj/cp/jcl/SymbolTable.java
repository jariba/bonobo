/*
 * SymbolTable.java
 *
 * Created on November 21, 2003, 4:52 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Map;

/**
 *
 * @author  Javier
 */
public interface SymbolTable 
{
    public SymbolTable getParent();
    
    public void addDataType(DataType dt);
    public DataType getDataType(String name);
    public Map getDataTypes();
    
    public void addVariable(Variable v);
    public Variable getVariable(String name);
    public Map getVariables();
    
    public void addMethod(JCLMethod m);
    public JCLMethod getExactMethod(String name,List parameterTypes);
    public JCLMethod getBestMethodMatch(String name,List argTypes);
    public Map getMethods();
    
    public void addConstraintFunction(JCLConstraintFunction f);
    public JCLConstraintFunction getConstraintFunction(String name,List parameterTypes);
    public Map getConstraintFunctions();    
}
