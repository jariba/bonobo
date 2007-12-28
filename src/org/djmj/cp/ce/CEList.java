/*
 * CEList.java
 *
 * Created on August 4, 2004, 11:43 AM
 */

package org.djmj.cp.ce;

import java.util.Vector;

/**
 *
 * @author  Javier
 */
public class CEList 
    extends Vector 
{
    public boolean containsValue(Object o)
    {
        return contains(o);
    }
}
