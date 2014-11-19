package org.jrivets.beans.api.model;

import java.lang.reflect.Method;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MethodInvokerTest {

    @Test
    public void transalteArgsTest() throws NoSuchMethodException, SecurityException {
        Method m = MethodInvokerTest.class.getMethod("emptyMethod");
        assertEquals(MethodInvoker.transalteArgs(m).length, 0);
        assertEquals(MethodInvoker.transalteArgs(m, new String[0]).length, 0);
        
        m = MethodInvokerTest.class.getMethod("inc", Integer.TYPE);
        assertEquals(MethodInvoker.transalteArgs(m, "10").length, 1);
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void transalteArgsFailsTest() throws NoSuchMethodException, SecurityException {
        Method m = MethodInvokerTest.class.getMethod("emptyMethod");
        MethodInvoker.transalteArgs(m, "asdf");
    }
    
    @Test
    public void invokeNoParamsTest() throws NoSuchMethodException, SecurityException {
        Method m = MethodInvokerTest.class.getMethod("emptyMethod");
        assertEquals(MethodInvoker.invoke(this, m), "empty");
    }
    
    @Test
    public void invokeOneParamsTest() throws NoSuchMethodException, SecurityException {
        Method m = MethodInvokerTest.class.getMethod("inc", Integer.TYPE);
        assertEquals(MethodInvoker.invoke(this, m, "10"), 11);
    }
    
    public int inc(int i) {
        return i + 1;
    }
    
    public String emptyMethod() {
        return "empty";
    }
    
    
}
