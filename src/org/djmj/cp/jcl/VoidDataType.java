/*
 * VoidDataType.java
 *
 * Created on January 11, 2004, 10:19 AM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class VoidDataType 
    implements DataType 
{
    static public VoidDataType instance = new VoidDataType();
    static public DataRef dataRef = new DataRef(instance, null);
      
    private VoidDataType() 
    {
    }
    
    public Object assign(Object lhsStorage, DataType rhsDT, Object rhsStorage) 
        throws Exception 
    {
        throw new Exception("Can't assign to void");
    }
    
    public Object createStorage() 
    {
        return null;
    }
    
    public DataType getMemberDataType(String memberName) 
        throws Exception 
    {
        throw new Exception("void doesn't have any members");
    }
    
    public DataRef getMemberValue(Object storage, String memberName) 
        throws Exception 
    {
        throw new Exception("void doesn't have any members");
    }
    
    public String getName() 
    {
        return "void";
    }
    
    public DataType getSuperclass() 
    {
        return null;
    }
    
    public Object getValue(Object storage) 
    {
        return null;        
    }
    
    public boolean isAssignableFrom(DataType rhsDT) 
    {
        return false;
    }
    
    public boolean isConstrained() 
    {
        return false;
    }
    
    public boolean isPrimitive() 
    {
        return true;
    }
    
    public Object newInstance() 
    {
        return null;
    }    
}
