/*
 * MethodInvokerException.java
 *
 * Created on October 23, 2002, 11:48 PM
 */

package org.djmj.util.reflection;

import java.lang.Throwable;
import java.lang.Exception;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author  Javier
 */
public class MethodInvokerException 
    extends Exception
{
    /** Creates a new instance of MethodInvokerException */
    public MethodInvokerException(Throwable t) 
    {
        super( t instanceof InvocationTargetException ? 
               ((InvocationTargetException)t).getTargetException() : 
               t );
    }
    
    public MethodInvokerException(String msg) 
    {
        super(msg);
    }
    
}
