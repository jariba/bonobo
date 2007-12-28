/*
 * SCFMemberOf.java
 *
 * Created on August 5, 2004, 5:14 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Vector;
import org.djmj.cp.ce.CENode;
import org.djmj.cp.ce.CEConstraint;
import org.djmj.cp.ce.CEList;
import org.djmj.cp.ce.CECMemberOf;

/**
 *
 * @author  Javier
 */
public class SCFMemberOf 
    extends JCLConstraintFunction 
{   
    static Vector parameterDTs_;
    static {
        parameterDTs_ = new Vector();
        parameterDTs_.add(PrimitiveDataType.PRIMITIVE); // TODO: need to support objects as well
        parameterDTs_.add(SDTList.getInstance());
    }
    
    public SCFMemberOf() 
    {
        super("member_of", parameterDTs_);
    }
    
    public CEConstraint makeConstraint(JCLParserContext context, List args) 
        throws Exception
    {
        // TODO: return new CECMemberOf object
        CExpr arg = (CExpr)args.get(0);
        CENode attr = arg.makeOutputNode(context); // TODO: make sure this is an lval?
        arg = (CExpr)args.get(1);
        CEList values = (CEList)((JavaClassStorage)arg.makeOutputNode(context).getValue()).getJavaObject();
        return new CECMemberOf(attr,values);
    }    
}
