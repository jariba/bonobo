/*
 * JCLConstraintFunction.java
 *
 * Created on August 5, 2004, 4:56 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import org.djmj.cp.ce.CEConstraint;

/**
 *
 * @author  Javier
 * TODO; if all this does is create a corresponding constraint later on we can 
 * pass the constraint class as paramter and use reflection to create the new instance so that
 * we don't have to define a new class for each constraint that is exported
 */
public abstract class JCLConstraintFunction 
{
    protected String name_;
    protected List parameterDTs_;
    
    public JCLConstraintFunction(String name,List parameterDTs) 
    {
        name_ = name;
        parameterDTs_ = parameterDTs;
    }
    
    public String getName() { return name_; }
    public List getParameterDataTypes() { return parameterDTs_; }
    
    /*
     * the args are a List of CExprs
     */
    public abstract CEConstraint makeConstraint(JCLParserContext context,List args)
        throws Exception;            
}
