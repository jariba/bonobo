/*
 * SystemMethod.java
 *
 * Created on January 11, 2004, 12:02 AM
 */

package org.djmj.cp.jcl;

import java.util.List;

/**
 *
 * @author  Javier
 */
public abstract class SystemMethod 
    extends JCLMethod 
{
    public SystemMethod(String name, List parameterDTs, DataType returnDT) 
    {
        super(name, parameterDTs, returnDT);
    }    
}
