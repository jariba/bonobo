/*
 * JCLSymbolTable.java
 *
 * Created on November 21, 2003, 5:10 PM
 */

package org.djmj.cp.jcl;

import java.util.*;

/**
 *
 * @author  Javier
 */
public class JCLSymbolTable 
    implements SymbolTable 
{
    protected SymbolTable parent_;
    protected Map dataTypes_;
    protected Map variables_;
    protected Map methods_;
    protected Map constraintFunctions_;
    
    public JCLSymbolTable() 
    {
        this(null);
    }
    
    public JCLSymbolTable(SymbolTable parent) 
    {
        parent_ = parent;

        dataTypes_ = new HashMap();
        variables_ = new HashMap();              
        methods_ = new HashMap();
        constraintFunctions_ = new HashMap();
        initDataTypes();        
    }
    
    protected void initDataTypes()
    {
        addDataType(PrimitiveDataType.INTEGER);
        addDataType(PrimitiveDataType.LONG);
        addDataType(PrimitiveDataType.FLOAT);
        addDataType(PrimitiveDataType.DOUBLE);
        addDataType(PrimitiveDataType.STRING);
        addDataType(PrimitiveDataType.DATE);
        addDataType(VoidDataType.instance);

        addDataType(ClassDataType.JCLObject);        
        addDataType(SDTCObject.getInstance());
        addMethod(new SDTCObject.MEnforceConstraints());
        addMethod(new SDTCObject.MGetValidValues());
        
        addDataType(SDTList.getInstance());
        addMethod(new SDTList.MListAdd());
        addMethod(new SDTList.MListGet());
        addMethod(new SDTList.MListRemove());
        
        addConstraintFunction(new SCFMemberOf());
    }
    
    public SymbolTable getParent() { return parent_; }
    
    /*
     * Data Types
     */
    public void addDataType(DataType dt) 
    { 
        dataTypes_.put(dt.getName(),dt); 
    }
    
    public DataType getDataType(String name) 
    { 
        // Data Types can only be defined at the top level
        if (parent_ != null)
            return parent_.getDataType(name);
        
        return (DataType) dataTypes_.get(name); 
    }        
    
    public Map getDataTypes() 
    { 
        // Data Types can only be defined at the top level
        if (parent_ != null)
            return parent_.getDataTypes();
        
        return dataTypes_; 
    }
    
    /*
     * Variables
     */
    public void addVariable(Variable v) 
    { 
        variables_.put(v.getName(),v); 
    }
    
    public Variable getVariable(String name) 
    { 
        // If a variable isn't found here, it could be defined in the parent
        // and still accessible to this scope
        Variable v = (Variable) variables_.get(name); 
        if (v==null && parent_!=null)
            return parent_.getVariable(name);
        
        return v;        
    }    
    
    public Map getVariables() 
    { 
        if (parent_ == null)
            return variables_; 
        
        Map retval = new HashMap();
        retval.putAll(variables_);
        retval.putAll(parent_.getVariables());
        
        return retval;
    }            
    
    
    /*
     * JCLMethods are stored in a 2 dimensional map
     * The first dimension is indexed by (name,numberOfArgs)
     * The second dimension is a set that is ordered by the specificity
     * of the arguments, from left to right
     */
    public void addMethod(JCLMethod m)
    {
        List key = makeMethodKey(m);
        SortedSet matches = (SortedSet)methods_.get(key);
        if (matches == null) {
            matches = new TreeSet(new MethodComparator());
            methods_.put(key,matches);
        }
        // TODO: make sure redefinition is handled correctly
        matches.add(m);
    }
    
    public JCLMethod getExactMethod(String name,List parameterTypes)
    {
        SortedSet matches = (SortedSet)getMethods().get(makeMethodKey(name, parameterTypes));
        if (matches == null) 
            return null;
        
        Iterator it = matches.iterator();
        while (it.hasNext()) {
            JCLMethod m = (JCLMethod)it.next();
            if (m.getParameterDataTypes().equals(parameterTypes))
                return m;
        }
        
        return null;
    }
    
    public JCLMethod getBestMethodMatch(String name,List argTypes)
    {
        SortedSet matches = (SortedSet)getMethods().get(makeMethodKey(name, argTypes));
        if (matches == null) 
            return null;

        // The MethodComparator will give us the matches in order of specificity
        Iterator it = matches.iterator();
        while (it.hasNext()) {
            JCLMethod m = (JCLMethod)it.next();
            List parameterTypes = m.getParameterDataTypes();
            int i;
            for (i=0;i<parameterTypes.size();i++) {
                DataType parameterType = (DataType)parameterTypes.get(i);
                DataType argType = (DataType)argTypes.get(i);
                if (!parameterType.isAssignableFrom(argType))
                    break;
            }
            if (i==parameterTypes.size())
                return m;
        }
        
        return null;
    }
    
    public Map getMethods() 
    { 
        // Methods can only be defined at the top level
        if (parent_ != null)
            return parent_.getMethods();
        
        return methods_; 
    }

    protected List makeMethodKey(JCLMethod m)
    {
        return makeMethodKey(m.getName(),m.getParameterDataTypes());
    }
    
    protected List makeMethodKey(String name,List parameterTypes)
    {
        List key = new Vector();
        key.add(name);
        key.add(new Integer(parameterTypes.size()));
        
        return key;
    }
    
    protected static class MethodComparator
        implements Comparator
    {
        /*
         * This can only be called on methods with the same name
         * and same number of args
         */
        public int compare(Object o1, Object o2) 
        {
            JCLMethod lhs = (JCLMethod)o1;
            JCLMethod rhs = (JCLMethod)o2;
            
            List lhsTypes = lhs.getParameterDataTypes();
            List rhsTypes = rhs.getParameterDataTypes();
            
            for (int i=0;i<lhsTypes.size();i++) {
                DataType lhsType = (DataType)lhsTypes.get(i);
                DataType rhsType = (DataType)rhsTypes.get(i);
                
                if (lhsType == rhsType)
                    continue;
                
                if (lhsType.isPrimitive() || rhsType.isPrimitive())
                    continue;
                
                if (lhsType.isAssignableFrom(rhsType)) {
                    printDebugRelation(lhs,rhs,"GREATER_THAN");
                    return 1; // lhs > rhs
                }
                
                if (rhsType.isAssignableFrom(lhsType)) {
                    printDebugRelation(lhs,rhs,"LESS_THAN");
                    return -1; // lhs < rhs             
                }
                
                printDebugRelation(lhs,rhs,"INDIFERENT_TO");
                return lhsType.getName().compareTo(rhsType.getName());
            }
            
            printDebugRelation(lhs,rhs,"EQUAL_TO");
            return 0; // lhs == rhs, or don't care, so make them equal
        }        
        
        protected void printDebugRelation(JCLMethod lhs,JCLMethod rhs,String relation)
        {
            System.out.println(lhs.toString());
            System.out.println("is "+relation);
            System.out.println(rhs.toString());
        }
    }
    
    public void addConstraintFunction(JCLConstraintFunction f)
    {
        List key = makeMethodKey(f.getName(),f.getParameterDataTypes());
        constraintFunctions_.put(key,f);
    }
    
    public JCLConstraintFunction getConstraintFunction(String name,List parameterTypes)
    {
        return (JCLConstraintFunction)
            constraintFunctions_.get(makeMethodKey(name,parameterTypes));
    }
    
    public Map getConstraintFunctions()
    {
        return constraintFunctions_;
    }
}

