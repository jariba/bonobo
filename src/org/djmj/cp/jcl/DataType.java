/*
 * DataType.java
 *
 * Created on November 21, 2003, 4:08 PM
 */

package org.djmj.cp.jcl;

/**
 *
 * @author  Javier
 * A DataType in JCL is a class. 
 * In similar fashion to Java, primitive types are sort of "crippled" classes, 
 * but still need to support the same interface.
 * Any other special data types (like constrained types and wrappers for java classes
 * exported into JCL) are also KCL classes and must support this interface.
 */
public interface DataType 
{  
    public String getName();
    public DataType getSuperclass();
    public Object createStorage(); // Creates storage for reference to an instance of this DataType
    public Object newInstance();   
    public Object getValue(Object storage);
    public DataRef getMemberValue(Object storage,String memberName) throws Exception;
    public DataType getMemberDataType(String memberName) throws Exception;
    public Object assign(Object lhsStorage,DataType rhsDT,Object rhsStorage) throws Exception;
    public boolean isAssignableFrom(DataType rhsDT);
    public boolean isPrimitive();
    public boolean isConstrained();
}
