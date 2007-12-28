/*
 * PrimitiveDataType.java
 *
 * Created on November 21, 2003, 4:22 PM
 */

package org.djmj.cp.jcl;

import java.lang.reflect.*;

/**
 *
 * @author  Javier
 */
public class PrimitiveDataType 
    implements DataType 
{    
    public static final DataType PRIMITIVE = new JCLPrimitive();
    public static final PrimitiveDataType INTEGER = new PrimitiveDataType("int" , Integer.class);
    public static final PrimitiveDataType LONG    = new PrimitiveDataType("long"    , Long.class);
    public static final PrimitiveDataType FLOAT   = new PrimitiveDataType("float"   , Float.class);
    public static final PrimitiveDataType DOUBLE  = new PrimitiveDataType("double"  , Double.class);
    public static final PrimitiveDataType STRING  = new PrimitiveDataType("string"  , String.class);
    // HACK!! Need to add real support for dates
    public static final PrimitiveDataType DATE    = new PrimitiveDataType("date"    , Long.class);
    
    protected String name_;
    protected Class class_;
    
    public PrimitiveDataType(String name,Class c) 
    {
        class_=c;
        name_=name;
    }
    
    public String getName() { return name_; }
    public DataType getSuperclass() { return null; }
    public boolean isPrimitive() { return true; }    
    public boolean isConstrained() { return false; }
    public Class getJavaClass() { return class_; }
    
    public Object createStorage() 
    {
        return null;
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
        
        // Primitive types create a copy when they're assigned 
        if (isNumber(class_))
            return castPrimitiveNumber(rhsDT.getValue(rhsStorage));
        
        Object rhsValue = rhsDT.getValue(rhsStorage).toString();
        Constructor c = class_.getConstructor(new Class[]{String.class});
        System.out.println("rhs is "+rhsValue.getClass().getName()+" "+rhsValue+"\n");
        return c.newInstance(new Object[]{rhsValue}); // new value for lhsStorage
    }
    
    public Object getValue(Object storage) 
    {
        return storage;
    }

    public DataRef getMemberValue(Object storage,String memberName)
        throws Exception
    {
        throw new Exception("Primitive type "+getName()+" doesn't have any members");
    }

    public DataType getMemberDataType(String memberName) 
        throws Exception 
    {
        throw new Exception("Primitive type "+getName()+" doesn't have any members");
    }
        
    public boolean isAssignableFrom(DataType rhsDT) 
    {
        if (this == rhsDT)
            return true;   
        
        if (isNumber(class_) && rhsDT.isPrimitive()) {
            Class rhsJavaClass = ((PrimitiveDataType)rhsDT).getJavaClass();

            // TODO: are there any Number subclasses that aren't assignable
            // between them?
            return isNumber(rhsJavaClass);
        }
        
        return false;
    }            
    
    protected Object castPrimitiveNumber(Object value)
    {
        try {
            Method m = value.getClass().getMethod(getName()+"Value", null);
            return m.invoke(value,null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected boolean isNumber(Class c)
    {
        return Number.class.isAssignableFrom(c);
    }
    
    public boolean isNumber()
    {
        return isNumber(class_);
    }
    
    /*
     * this is an abstract base class so that generic methods can be 
     * defined on primitive types
     */
    public static class JCLPrimitive
        implements DataType
    {
        public String getName() { return "kclprimitive"; }
        public DataType getSuperclass() { return null; }
        public Object createStorage() { return null; }
        
        public Object newInstance() 
        { 
            throw new RuntimeException("Can't call makeNewObject on "+getName());
        }   
        
        public Object getValue(Object storage) { return null; }
        
        public DataRef getMemberValue(Object storage,String memberName)
            throws Exception
        {
            throw new Exception("Primitive type "+getName()+" doesn't have any members");
        }

        public DataType getMemberDataType(String memberName) 
            throws Exception 
        {
            throw new Exception("Primitive type "+getName()+" doesn't have any members");
        }
        
        public Object assign(Object lhsStorage,DataType rhsDT,Object rhsStorage) 
            throws Exception
        {
            throw new Exception("Can't assign "+getName());
        }
        
        /* this allows the user to define some methods that will parse ok but can bomb at 
         * runtime if the args don't match exactly, we'll have to leave with it for now
         * Initially this class will only be used in System methods to support Collections
         * so we should be safe
         */
        public boolean isAssignableFrom(DataType rhsDT)
        {
            return rhsDT.isPrimitive();
        }
        
        public boolean isPrimitive()   { return true; }
        public boolean isConstrained() { return false;}
    }
}
