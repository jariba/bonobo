/*
 * DataRef.java
 *
 * Created on November 21, 2003, 11:22 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class DataRef 
{
    protected DataType dataType_;
    protected DataType rtDataType_; // Run-time data type, may change for objects, not primitives
    protected Object storage_;      // always use get/setStorage methods to change storage
                                    // this member is only used in base default impl
    
    public DataRef(DataType dt, Object storage)     
    {
        dataType_ = dt;
        rtDataType_ = dt;
        storage_ = storage;
    }

    public DataRef(DataType dt)     
    {
        this(dt,dt.createStorage());
    }
    
    public DataRef(DataRef ref)
        throws Exception
    {        
        this(ref.getDataType());
        assign(ref);
    }

    public DataType getDataType() { return dataType_; }
    public DataType getRuntimeDataType() { return rtDataType_; }
    public Object getValue() { return dataType_.getValue(getStorage()); }    

    public void assign(DataRef rhs)
        throws Exception
    {
        setStorage(dataType_.assign(getStorage(),rhs.getDataType(),rhs.getStorage()));

        // run-time data type for object references changes with assignment
        // this is required to support polymorphism
        if (!dataType_.isPrimitive())
            rtDataType_=rhs.getDataType();        
    }
    
    protected Object getStorage() { return storage_; }        
    protected void setStorage(Object o) { storage_=o;  }      

    /*
     * Compare values to determine if 2 DataRefs are equal
     */
    public boolean equals(Object obj)    
    {
        if (obj==null)
            return false;
        
        if (obj instanceof DataRef) {
            DataRef rhs = (DataRef)obj;

            if (getValue()==null)
                return false;
            
            return getValue().equals(rhs.getValue());
        }
        
        return false;
    }
    
    public int hashCode()    
    {
        if (getValue() == null)
            return 0;
        
        return getValue().hashCode();
    }
    
    public String toString()
    {
        if (!dataType_.isPrimitive())
            return dataType_.getName()+":"+getStorage();
        else 
            return ""+getStorage();
    }
}
