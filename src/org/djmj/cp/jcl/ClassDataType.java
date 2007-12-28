/*
 * ClassDataType.java
 *
 * Created on November 23, 2003, 12:34 AM
 */

package org.djmj.cp.jcl;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author  Javier
 * Storage for a class is a (DataType,DataRef) Map for its members
 *
 */
public class ClassDataType 
    implements DataType
{    
    String name_;
    ClassDataType superclass_;
    Map members_;

    public static final ClassDataType JCLObject = new ClassDataType("object");

    protected ClassDataType(String name)
    {
        name_ = name;
        superclass_=null;
        members_ = new TreeMap();
    }
    
    public ClassDataType(String name,ClassDataType superclass,Map members) 
    {
        name_ = name;
        if (superclass != null)
            superclass_ = superclass;
        else
            superclass_ = JCLObject;
        
        members_ = members;
    }
    
    public String getName() { return name_; }
    public boolean isPrimitive() { return false; }    
    public boolean isConstrained() { return false; }
    public DataType getSuperclass() { return superclass_; }
    
    // TODO: add superclass members? who's going to use this?
    public Map getMembers() { return members_; }    
    
    public Object createStorage() 
    {
        if (superclass_ != null)
            return superclass_.createStorage();
        
        return null;
    }

    public Object newInstance()
    {
        ClassStorage newInstance;
        
        if (superclass_ != null)
            newInstance = (ClassStorage)superclass_.newInstance();
        else
            newInstance = new ClassStorage();
        
        Iterator it = members_.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String name = (String)entry.getKey();
            DataType dt = (DataType)entry.getValue();
            newInstance.addMember(name,dt);
        }
        
        return newInstance;
    }    
    
    public Object getValue(Object storage) { return storage; }

    protected DataType findMemberDataType(String memberName)
    {
        DataType dt = (DataType)members_.get(memberName);
        if (dt != null)
            return dt;
        
        if (superclass_ != null)
            return superclass_.findMemberDataType(memberName); 
        
        return null;
    }
    
    public DataType getMemberDataType(String memberName) 
        throws Exception 
    {
        DataType dt = findMemberDataType(memberName);
        if (dt == null)
            throw new Exception("Member "+memberName+" can't be found in object of type : "+getName());
        
        return dt;
    }
    
    public DataRef getMemberValue(Object storage,String memberName)
        throws Exception
    {
        if (storage==null)
            throw new Exception("Can't access member on null object");
        ClassStorage obj = (ClassStorage)storage;
        DataRef ref = obj.getMemberRef(memberName);        
        if (ref==null)
            throw new Exception("Member "+memberName+" can't be found in object of type : "+getName());
        
        return ref;
    } 
    
    public Object assign(Object lhsStorage, DataType rhsDT, Object rhsStorage) 
        throws Exception 
    {
        if (!isAssignableFrom(rhsDT))
            throw new Exception("Can't assign from "+rhsDT.getName()+" to "+getName());
        
        return rhsDT.getValue(rhsStorage); // new value for lhsStorage
    }    
    
    public boolean isAssignableFrom(DataType rhsDT) 
    {
        if (rhsDT.isPrimitive())
            return false;
        
        DataType c = rhsDT;
        while (c!=null) {
            if (c == this)
                return true;
            c = c.getSuperclass();
        }
        
        return false;
    }      
}
