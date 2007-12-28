/*
 * JCLLocalClient.java
 *
 * Created on December 15, 2003, 10:26 AM
 */

package org.djmj.cp.jcl.client;

import java.io.StringReader;
import java.util.Date;

import org.djmj.cp.jcl.*;
import org.djmj.util.DateUtils;
import antlr.TokenBuffer;

/**
 *
 * @author  Javier
 */
public class JCLLocalClient 
    implements JCLClient 
{
    protected JCLParser parser_;
        
    public JCLLocalClient(JCLParser parser) 
    {
        parser_ = parser;
    }
    
    public JCLParser getParser() { return parser_; }
    public void setParser(JCLParser p) { parser_ = p; }
    
    public void evalProgram(String kclProgram) 
        throws Exception 
    {
        StringReader input = new StringReader(kclProgram);        
        parser_.setTokenBuffer(new TokenBuffer(new JCLLexer(input)));
        parser_.program();                        
    }
    
    public Object evalExpr(String kclExpr) 
        throws Exception 
    {
        StringReader input = new StringReader(kclExpr);        
        parser_.setTokenBuffer(new TokenBuffer(new JCLLexer(input)));
        Expr expr = parser_.expression();  
        DataRef result = expr.eval(parser_.context_);
        Object retval = result.getValue();
        if (retval instanceof JavaClassStorage) { 
            // the DataRef points to a wrapped Java object, 
            // extract the actual java object before returning it
            return ((JavaClassStorage)retval).getJavaObject();
        }
        else
            return retval;
    } 
    
    public String getJCLLiteral(String kclType,Object value)
    {
        if (value == null)
            return "null";
        
        if (kclType.equals("string"))
            return "\""+value+"\"";

        // TODO: dates are supported as longs in JCL for now
        if (kclType.equals("date")) {
            if (value instanceof Date)
                return dateToJCLLiteral((Date)value);
            
            return dateToJCLLiteral(DateUtils.stringToDate(value.toString()));
        }
        
        if (kclType.equals("int")) {
            if (value instanceof Integer)
                return integerToJCLLiteral((Integer)value);
            
            return integerToJCLLiteral(new Integer(value.toString()));
        }

        return value.toString();
    }    
    
    /**
     * Transform an integer to a valua understood by JCL.
	 * @param integer the integer to be converted
	 * @return An String representation for the integer
	 */
	private String integerToJCLLiteral(Integer integer) {
		return integer.toString();
	}

	protected String dateToJCLLiteral(Date d)
    {
        return new Long(d.getTime()).toString();
    }
}
