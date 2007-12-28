/*
 * Test.java
 *
 * Created on November 12, 2003, 4:31 PM
 */

package org.djmj.cp.ce;

import java.util.List;

/**
 *
 * @author  Javier
 */
public class Test 
{
    public static void main(String args[])
    {
        Test t = new Test();
        t.run();
    }
    
    public Test() 
    {
    }
    
    public void run()
    {
        CENode a = new CENode();
        CENode b = new CENode();
        
        CEConstraint eq= new CECRelational("==",a,b,new CENode());
        CObject obj = new CObject();
        obj.addConstraint(eq);
        obj.activateConstraints();
        
        displayCObject(obj);   
        
        System.out.println("Activated Constraint ");
        eq.activate();
        displayCObject(obj);   

        System.out.println("a=3");
        a.setValue(new Integer(3));
        displayCObject(obj);   
        
        System.out.println("b=3");
        b.setValue(new Integer(3));
        displayCObject(obj);   
        
        System.out.println("b=2");
        b.setValue(new Integer(2));
        displayCObject(obj);   

        System.out.println("Deactivated Constraint ");
        eq.deactivate();
        displayCObject(obj);   

        System.out.println("Activated Constraint ");
        eq.activate();
        displayCObject(obj); 
        
        System.out.println("b=3");
        b.setValue(new Integer(3));
        displayCObject(obj);           
    }
    
    protected void displayCObject(CObject o)
    {
        System.out.println("CObject {");
        System.out.println("  Conflict:"+o.getConflict());
        System.out.println("  Violations:"+o.getViolations());
        System.out.println("  Constraints:");
        List constraints = o.getConstraints();
        for (int i=0;i<constraints.size();i++) {
            CEConstraint c = (CEConstraint)constraints.get(i);
            displayConstraint(c);
        }
        System.out.println("}");
    }
    
    protected void displayConstraint(CEConstraint c)
    {
        System.out.println("    Constraint "+c.getClass().getName()+" {");
        System.out.println("      active:"+c.isActive());
        if (c.isActive())
            System.out.println("      conflict:"+c.getConflictStr());        
        if (c.getConflict()!=CEConstraint.NO_CONFLICT)
            System.out.println("      violation:"+c.getViolationInfo());                    
        System.out.println("  }");        
    }
}
