/*
 * Method.java
 *
 * Created on January 10, 2004, 7:25 PM
 */

package org.djmj.cp.jcl;

import java.util.List;

/**
 *
 * @author  Javier
 */
public abstract class JCLMethod 
{
    protected String name_;
    protected List parameterDTs_;
    protected DataType returnDT_;
    
    public JCLMethod(String name, List parameterDTs, DataType returnDT) 
    {
        name_ = name;
        parameterDTs_ = parameterDTs;
        returnDT_ = returnDT;
    }
    
    public String getName() { return name_; }
    public List getParameterDataTypes() { return parameterDTs_; }
    public DataType getReturnDataType() { return returnDT_; }
    
    public boolean isSystemMethod() { return (this instanceof SystemMethod); }
    
    public abstract DataRef eval(List args,JCLParserContext context) 
        throws Exception;
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Method "+getName()+" (");
        for (int i=0;i<parameterDTs_.size();i++) {
            if (i>0)
                buf.append(",");
            buf.append(((DataType)parameterDTs_.get(i)).getName());
        }
        buf.append(") ");
        buf.append(returnDT_.getName());
        
        return buf.toString();
    }
}
