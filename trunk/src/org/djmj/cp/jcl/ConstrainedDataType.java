/*
 * ConstrainedDataType.java
 *
 * Created on November 23, 2003, 7:49 PM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CENode;


/**
 *
 * @author  Javier
 */
public class ConstrainedDataType 
    implements DataType 
{    
    protected DataType dt_;
    
    public ConstrainedDataType(DataType dt) 
    {
        dt_ = dt;
    }
    
    public String getName() { return "constrained "+dt_.getName(); }
    public DataType getSuperclass() { return dt_.getSuperclass(); }    
    public boolean isPrimitive() { return dt_.isPrimitive(); }    
    public boolean isConstrained() { return true; }
    
    public Object createStorage() 
    {
        CENode node = new CENode();
        node.setValue(dt_.createStorage());
        
        return node;
    }
    
    public Object newInstance() 
    {
        // TODO: make this an assert
        throw new RuntimeException("Can't call makeNewObject on "+this.getClass().getName());
    }
    
    public Object assign(Object lhsStorage, DataType rhsDT, Object rhsStorage) 
        throws Exception 
    {
        if (!isAssignableFrom(rhsDT))
            throw new Exception("Can't assign from "+rhsDT.getName()+" to "+getName());
        
        CENode node = getCENode(lhsStorage);
        node.setValue(dt_.assign(node.getValue(),rhsDT,rhsStorage));
        return lhsStorage;
    }
    
    public Object getValue(Object storage) 
    {
        return dt_.getValue(getCENode(storage).getValue());
    }
    
    public DataType getDataType() { return dt_; } 
    
    public boolean isAssignableFrom(DataType rhsDT) 
    {
        if (rhsDT.isConstrained()) 
            return dt_.isAssignableFrom(((ConstrainedDataType)rhsDT).getDataType());
        
        return dt_.isAssignableFrom(rhsDT);
    }
        
    protected CENode getCENode(Object storage)
    {
        CENode node=(CENode)storage;
        // TODO: assert(node!=null);
        return node;
    }
    
    public DataRef getMemberValue(Object storage, String memberName) 
        throws Exception 
    {
        return dt_.getMemberValue(storage, memberName);
    }        
    
    public DataType getMemberDataType(String memberName) 
        throws Exception 
    {
        return dt_.getMemberDataType(memberName);
    }    
}
