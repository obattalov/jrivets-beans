package org.jrivets.kvstorage;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class InMemoryKeyValueStorageTest {

    private InMemoryKeyValueStorage<Integer, A> store;
    
    static class A {
        String val;
    }
    
    @BeforeMethod
    public void init() {
        store = new InMemoryKeyValueStorage<>();
    }
    
    @Test
    public void putGetTest() {
        A a = new A();
        a.val = "abc";
        store.put(1, a);
        assertEquals(a.val, store.get(1).value.val);
        assertEquals(store.get(1).version, 1);
        assertNotEquals(a, store.get(1));
        
        store.put(1, a);
        assertEquals(a.val, store.get(1).value.val);
        assertEquals(store.get(1).version, 2);
        
        store.remove(1);
        store.put(1, a);
        assertEquals(store.get(1).version, 1);
    }
 
    @Test
    public void casTest() {
        A a = new A();
        a.val = "abc";
        assertTrue(store.cas(1, a, -1) < 0);
        assertNull(store.get(1));
    
        store.put(1, a);
        a.val = "czd";
        assertTrue(store.cas(1, a, 2) < 0);
        assertEquals(store.cas(1, a, 1), 2);
        assertEquals(a.val, store.get(1).value.val);
        assertEquals(store.get(1).version, 2);
    }

    @Test
    public void removeVersionedTest() {
        A a = new A();
        a.val = "abc";
        store.put(1, a);
        assertNull(store.remove(1, 2));
        assertEquals(store.remove(1, 1).value.val, a.val);
        
        store.put(1, a);
        assertEquals(store.remove(1, 1).version, 1);
    }
}
