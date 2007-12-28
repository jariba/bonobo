/*
 * SDTCObject.java
 *
 * Created on January 11, 2004, 12:09 AM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import org.djmj.cp.ce.CObject;
import org.djmj.cp.ce.CENode;

/**
 *
 * @author  Javier
 */
public class SDTCObject 
    extends JavaClassDataType 
{
    public static SDTCObject instance_=null;
    
    private SDTCObject(Map javaMembers) 
    {
        super("cobject",CObject.class,javaMembers);
    }    
    
    static public SDTCObject getInstance()
    {
        if (instance_==null) {
            Map members = new HashMap();
            members.put("Conflict",PrimitiveDataType.DOUBLE);
            members.put("Violations",PrimitiveDataType.STRING);
            instance_ = new SDTCObject(members);
        }
        
        return instance_;
    }
    
    static public class MEnforceConstraints
        extends SystemMethod
    { 
        public static List parameterDTs_;
        
        static {
            parameterDTs_ = new Vector();
            parameterDTs_.add(SDTCObject.getInstance());
        }
        
        public MEnforceConstraints() 
        {
            super("enforce_constraints", parameterDTs_, VoidDataType.instance);
        }    
        
        public DataRef eval(List args, JCLParserContext context) 
            throws Exception 
        {
            DataRef ref = (DataRef)args.get(0);            
            CObject cobj = (CObject)((JavaClassStorage)ref.getValue()).getJavaObject();    
            if (cobj==null)
                throw new Exception("Can't activate constraints on null object");            
            cobj.activateConstraints();
            
            return VoidDataType.dataRef;
        }    
    }

    /*
     * Get valid values for a constrained variable
     */
    static public class MGetValidValues
        extends SystemMethod
    { 
        public static List parameterDTs_;
        
        static {
            parameterDTs_ = new Vector();
            parameterDTs_.add(PrimitiveDataType.PRIMITIVE);
        }
        
        public MGetValidValues() 
        {
            super("get_valid_values", parameterDTs_, SDTList.getInstance());
        }    
        
        public DataRef eval(List args, JCLParserContext context) 
            throws Exception 
        {                        
            DataRef ref = (DataRef)args.get(0); // Constrained node           
            if (!ref.getDataType().isConstrained())
                throw new Exception("get_valid_values can only be called on constrained objects");
            
            CENode node = ((ConstrainedDataType)ref.getDataType()).getCENode(ref.getStorage());
                        
            DataRef retval = new DataRef(
                SDTList.getInstance(),
                SDTList.getInstance().newInstance()
            );
            JCLList newList = (JCLList)((JavaClassStorage)retval.getValue()).getJavaObject();
            node.getDiscreteValidValues(newList);
            return retval;
        }    
    }
    
}


