/*
 * ClassStorage.java
 *
 * Created on November 24, 2003, 10:37 AM
 */

package org.djmj.cp.jcl;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.*;

/**
 *
 * @author  Javier
 * Storage for a Java Class exported into JCL
 */
public class JavaClassStorage 
    extends ClassStorage
{    
    Class javaClass_;
    Object javaObject_;
    
    public JavaClassStorage(Class c) 
    {
        try {
            javaClass_ = c;
            javaObject_ = c.newInstance();
        }
        catch (Exception e) {
            // This should never happen, log it anyway
            e.printStackTrace(System.err);
        }
    }
    
    public Object getJavaObject() { return javaObject_; }
    
    // TODO: add other properties, like whether the member is Read-Only, etc.
    public void addJavaMember(String name,DataType dt)
    {
        DataRef dref = new JavaDataRef(this,name,dt);
        members_.put(name,dref);
    }
    
    protected Object getJavaMemberValue(String name)
    {
        try {
            Method m = javaClass_.getMethod("get"+name,null);
            return m.invoke(javaObject_,null);
        }
        catch (Exception e) {
            // This should never happen, log it anyway
            e.printStackTrace(System.err);
            return null;
        }
    }
        
    protected Object setJavaMemberValue(String name,Object value)
    {
        try {
            // TODO: what if value is null?
            Method m = javaClass_.getMethod("set"+name,new Class[]{value.getClass()});
            return m.invoke(javaObject_,new Object[]{value});
        }
        catch (Exception e) {
            // This should never happen, log it anyway
            e.printStackTrace(System.err);
            return null;
        }
    }
        
    public DataRef getMemberRef(String memberName)
    {
        return (DataRef)members_.get(memberName);
    }
    
    public String toString()
    {
        return javaObject_.toString();
    }
    
    static private class JavaDataRef
        extends DataRef
    {
        protected JavaClassStorage jcs_;
        protected String name_;
        
        public JavaDataRef(JavaClassStorage jcs,String name,DataType dt)
        {
            super(dt,null);
            jcs_ = jcs;
            name_ = name;
        }
        
        protected Object getStorage() 
        { 
            return jcs_.getJavaMemberValue(name_);
        }          
        
        protected void setStorage(Object storage)
        {
            jcs_.setJavaMemberValue(name_,storage);
        }
    }    
}
