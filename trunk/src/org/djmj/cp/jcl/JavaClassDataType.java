/*
 * JavaClassDataType.java
 *
 * Created on November 24, 2003, 3:43 PM
 */

package org.djmj.cp.jcl;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author  Javier
 * DataType for a Java class made visible in JCL
 */
public class JavaClassDataType 
    extends ClassDataType 
{
    protected Class javaClass_;
    protected Map javaMembers_;
    
    public JavaClassDataType(String name,Class javaClass,Map javaMembers) 
    {
        super(name, null,new HashMap());
        javaClass_ = javaClass;
        javaMembers_ = javaMembers;
    }    
    
    public Object newInstance()
    {
        JavaClassStorage newInstance = new JavaClassStorage(javaClass_);
                
        Iterator it = javaMembers_.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String name = (String)entry.getKey();
            DataType dt = (DataType)entry.getValue();
            newInstance.addJavaMember(name,dt);
        }
        
        return newInstance;
    }  
    
    protected DataType findMemberDataType(String memberName)
    {
        DataType dt = (DataType)javaMembers_.get(memberName);
        if (dt != null)
            return dt;
        
        return super.findMemberDataType(memberName);
    }    
}
