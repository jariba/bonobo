/*
 * SDTList.java
 *
 * SDT=SystemDataType 
 * List
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import org.djmj.cp.ce.CObject;

/**
 *
 * @author  Javier
 */
public class SDTList 
    extends JavaClassDataType 
{
    public static SDTList instance_=null;
    
    private SDTList(Map javaMembers) 
    {
        super("list",JCLList.class,javaMembers);
    }    
    
    static public SDTList getInstance()
    {
        if (instance_==null) {
            Map members = new HashMap();
            members.put("Size",PrimitiveDataType.INTEGER);
            instance_ = new SDTList(members);
        }
        
        return instance_;
    }
    
    static public class MListAdd
        extends SystemMethod
    { 
        public static List parameterDTs_;
        
        static {
            parameterDTs_ = new Vector();
            parameterDTs_.add(SDTList.getInstance());         // List 
            parameterDTs_.add(PrimitiveDataType.PRIMITIVE);   // Element
        }
        
        public MListAdd() 
        {
            super("list_add", parameterDTs_, VoidDataType.instance);
        }    
        
        public DataRef eval(List args, JCLParserContext context) 
            throws Exception 
        {
            DataRef ref = (DataRef)args.get(0);
            JCLList list = (JCLList)((JavaClassStorage)ref.getValue()).getJavaObject();    
            if (list==null)
                throw new Exception("Can't add element to null list");  
            
            ref = (DataRef)args.get(1);
            list.add(ref);
            
            return VoidDataType.dataRef;
        }    
    }
    
    static public class MListRemove
        extends SystemMethod
    { 
        public static List parameterDTs_;
        
        static {
            parameterDTs_ = new Vector();
            parameterDTs_.add(SDTList.getInstance());       // List
            parameterDTs_.add(PrimitiveDataType.INTEGER);   // Index
        }
        
        public MListRemove() 
        {
            super("list_remove", parameterDTs_, VoidDataType.instance);
        }    
        
        public DataRef eval(List args, JCLParserContext context) 
            throws Exception 
        {
            DataRef ref = (DataRef)args.get(0);
            JCLList list = (JCLList)((JavaClassStorage)ref.getValue()).getJavaObject();    
            if (list==null)
                throw new Exception("Can't remove element from null list");  
            
            ref = (DataRef)args.get(1);
            list.remove(((Integer)ref.getValue()).intValue());
            
            return VoidDataType.dataRef;
        }    
    }    
    
    static public class MListGet
        extends SystemMethod
    { 
        public static List parameterDTs_;
        
        static {
            parameterDTs_ = new Vector();
            parameterDTs_.add(SDTList.getInstance());      // List
            parameterDTs_.add(PrimitiveDataType.INTEGER);  // Index
        }
        
        public MListGet() 
        {
            super("list_get", parameterDTs_, PrimitiveDataType.INTEGER);
        }    
        
        public DataRef eval(List args, JCLParserContext context) 
            throws Exception 
        {
            DataRef ref = (DataRef)args.get(0);
            JCLList list = (JCLList)((JavaClassStorage)ref.getValue()).getJavaObject();    
            if (list==null)
                throw new Exception("Can't get element from null list");  
            
            ref = (DataRef)args.get(1);
                        
            return (DataRef)list.get(((Integer)ref.getValue()).intValue());
        }    
    }    
}


