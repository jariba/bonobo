/*
 * CStmtFunction.java
 *
 * Created on August 5, 2004, 4:49 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import org.djmj.cp.ce.CEConstraint;

/**
 *
 * @author  Javier
 */
public class CStmtFunction 
    extends CStmtBase 
{
    protected JCLConstraintFunction function_;
    protected List args_;
    
    public CStmtFunction(JCLConstraintFunction f,List args) 
    {
        function_ = f;
        args_ = args;
    }
    
    protected CEConstraint makeConstraint(JCLParserContext context)
        throws Exception
    {
        return function_.makeConstraint(context,args_);
    }
}
