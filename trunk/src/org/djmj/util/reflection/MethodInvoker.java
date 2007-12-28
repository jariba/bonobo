/*
 * MethodInvoker.java
 *
 * Created on October 23, 2002, 8:47 PM
 */
package org.djmj.util.reflection;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 *
 * @author  Javier
 */
public class MethodInvoker 
{
    static private Logger LOG = Logger.getLogger(MethodInvoker.class);
    
    private MethodInvoker() 
    {
    }

    public static Object invoke(Object o,String methodName,Object methodArgs[])
        throws MethodInvokerException
    {
        try {
            Method m = findMethod(o.getClass(),methodName,getObjectTypes(methodArgs));
            return m.invoke(o,methodArgs);
        }
        catch (MethodInvokerException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MethodInvokerException(e);
        }
    }   

    private static Class[] getObjectTypes(Object objects[])
    {
        Class types[]=null;            
        if (objects != null) {
            types = new Class[objects.length];
            for (int i=0;i<objects.length;i++)
                types[i] = (objects[i]==null ? null : objects[i].getClass());
        }
        
        return types;
    }
    
    /*
     * Speedy shortcut, if user is willing to specify argTypes, we can skip all 
     * the parameter matching crud
     */
    public static Object invoke(Object o,String methodName,Object methodArgs[],Class argTypes[])
        throws MethodInvokerException
    {
        try {
            Method m = o.getClass().getMethod(methodName,argTypes);
            return m.invoke(o,methodArgs);
        }
        catch (Exception e) {
            throw new MethodInvokerException(e);
        }
    }
    
    private static Method findMethod(Class objectClass,String methodName,Class argTypes[])
        throws MethodInvokerException,Exception
    {          
        Method[] methods = objectClass.getMethods();
        int nArgs = (argTypes==null ? 0 : argTypes.length);

        for (int i=0;i<methods.length;i++) {
            if (methods[i].getName().equals(methodName) &&
                methods[i].getParameterTypes().length==nArgs) 
                   if (argTypesMatch(methods[i].getParameterTypes(),argTypes)) 
                        return methods[i];
        }

        throw new MethodInvokerException(getMethodNotFoundMsg(objectClass,methodName,argTypes));
    }
    
    private static boolean argTypesMatch(Class parameterTypes[],Class argTypes[])
        throws Exception
    {
        if (parameterTypes.length==0 && argTypes==null)
            return true;
        
        for (int i=0; i<parameterTypes.length; i++) {
            if (argTypes[i] != null &&
                !parameterTypes[i].isAssignableFrom(argTypes[i]))
                return false;
            if (argTypes[i] == null &&
                parameterTypes[i].isPrimitive())
                return false;
        }
        
        return true;
    }    
    
    private static String getMethodNotFoundMsg(Class objectClass,String methodName,Class argTypes[])
    {
        int nArgs = (argTypes==null ? 0 : argTypes.length);
        String msg = "\nCouldn't find match in class " + getClassName(objectClass) + "\n" +
                     "for method " + methodName + "\n" +
                     "with arg types (" + nArgs + ")\n";
        for (int i=0;i<nArgs;i++)
            msg += (argTypes[i]!=null ? argTypes[i].getName() : "null") + "\n";
            
        return msg;
    }
        
    private static String getClassName(Class c)
    {
        String name = c.getName();
        
        if (name.startsWith("$Proxy")) {
            for (;c != null;c = c.getSuperclass()) {
                Class ifaces[] = c.getInterfaces();
                for (int i=0;i<ifaces.length;i++)
                    if (ifaces[i].getName().startsWith("com.k3"))
                        return ifaces[i].getName();
            }
        }
        
        return name;
    }    
}
