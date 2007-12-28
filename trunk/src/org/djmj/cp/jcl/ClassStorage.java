/*
 * ClassStorage.java
 *
 * Created on November 24, 2003, 10:37 AM
 */

package org.djmj.cp.jcl;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author  Javier
 */
public class ClassStorage 
{
    protected Map members_; // name->DataRef map
    
    public ClassStorage() 
    {
        members_ = new HashMap();
    }
    
    public void addMember(String name,DataType dt)
    {
        DataRef dref = new DataRef(dt,dt.createStorage());
        members_.put(name,dref);
    }
    
    public DataRef getMemberRef(String memberName)
    {
        return (DataRef)members_.get(memberName);
    }
    
    public String toString()
    {
        return members_.toString();
    }
}
