/*
 * JCLParserOut.java
 *
 * Created on November 20, 2003, 6:18 PM
 */

package org.djmj.cp.jcl;

import java.util.List;
import java.util.Vector;


/**
 *
 * @author  Javier
 */
public class JCLParserOut 
{
    List observers_;
    
    public JCLParserOut()
    {
        observers_ = new Vector();
    }
    
    public void addObserver(JCLParserOutObserver o) { observers_.add(o); }
    public void removeObserver(JCLParserOutObserver o) { observers_.remove(o); }
    
    public void println(String s)
    {
        for (int i=0; i<observers_.size(); i++) {
            JCLParserOutObserver o = (JCLParserOutObserver)observers_.get(i);
            o.onPrintln(s);
        }
    }
    
    public static class JCLPOConsoleObserver
        implements JCLParserOutObserver
    {        
        public void onPrintln(String s) 
        {
            System.out.println(s);
        }        
    }
}
