/*
 * JCLList.java
 *
 * Created on August 5, 2004, 10:12 PM
 */

package org.djmj.cp.jcl;

import org.djmj.cp.ce.CEList;

/**
 *
 * @author  Javier
 */
public class JCLList 
    extends CEList
{
    /*
     * A JCLList contains DataRefs, so to look for a value we need to
     * get inside the DataRefs
     */
    public boolean containsValue(Object o)
    {
        for (int i=0;i<size();i++) {
            DataRef ref = (DataRef) get(i);
            if (ref.getValue().equals(o))
                return true;
        }
            
        return false;
    }
    
    public Integer getSize() { 
        return new Integer(size()); 
    }
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        
        buf.append("{");
        for (int i=0;i<size();i++) {
            if (i>0)
                buf.append(",");
            buf.append(get(i));
        }
        buf.append("}");
        
        return buf.toString();
    }
}
