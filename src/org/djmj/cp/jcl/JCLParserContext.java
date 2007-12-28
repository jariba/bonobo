/*
 * JCLParserContext.java
 *
 * Created on January 7, 2004, 10:02 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 */
public class JCLParserContext 
{
    protected JCLParserOut out_;
    protected SymbolTable stable_;
    
    public JCLParserContext(SymbolTable stable,JCLParserOut out) 
    {
        stable_ = stable;
        out_ = out;
    }
    
    public JCLParserOut getOut() { return out_; }
    public void setOut(JCLParserOut out) { out_=out; }
    
    public SymbolTable getSymbolTable() { return stable_; }    
    public void setSymbolTable(SymbolTable st) { stable_=st; }    
}
