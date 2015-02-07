package org.jrivets.util;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BinarySerializerTest {

    static class A {
        int v;
    }
    
    static class B extends A {
        private String c;
    }
    
    @Test
    public void copyNullTest() {
        assertNull(BinarySerializer.copy(null));
    }
    
    @Test
    public void copyTest() {
        A a = new A();
        a.v = 23;
        A a2 = BinarySerializer.copy(a);
        assertEquals(a2.v, a.v);
        assertNotEquals(a2, a);
        
        B b = new B();
        b.v = 11;
        b.c = "abcz";
        B b2 = BinarySerializer.copy(b);
        assertEquals(b2.v, b.v);
        assertEquals(b2.c, b.c);
        assertNotEquals(b2, b);
    }
    
}
