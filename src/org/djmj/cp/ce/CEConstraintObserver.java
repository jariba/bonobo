/*
 * CEConstraintObserver.java
 *
 * Created on August 11, 2004, 4:10 PM
 */

package org.djmj.cp.ce;

/**
 *
 * @author  Javier
 */
public interface CEConstraintObserver 
{
    public void onNodeChanged(CEConstraint c,CENode n);    
}
