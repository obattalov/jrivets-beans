package org.jrivets.beans.api.model;

import org.jrivets.beans.Operation;
import org.jrivets.beans.Property;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TypeScannerTest {
    
    private static class Simple {
        
        @Property
        String property;
        
        int prop1;
        
        @Property
        int getProperty1() {
            return prop1;
        }
        
        @Property
        void setProperty1(int p1) {
            this.prop1 = p1;
        }
        
        @Operation
        int add(int i) {
            return prop1 + i;
        }
    }
    
    private static class ErrWrongAnnotated {
        
        @Property
        @Operation
        public int getVal() {
            return 1;
        }
        
    }

    private static class ErrSamePropName {
        
        @Property
        public void setVal(int i) {
        }

        @Property
        public void setval(int i) {
        }
    }
    
    private static class ErrOverloadOp extends Simple {
        @Operation
        int aDD(int i) {
            return prop1 + i;
        }
    }
    
    private static class PropertySetter extends Simple {
        @Property
        void setproperty(String s) {
            this.property = s + "1";
        }
    }
    
    private static class PropertyGetter extends Simple {
        @Property
        String getproperty() {
            return property + "1";
        }
    }
    
    @Test
    public void noAnnotationsTest() {
        TypeScanner ts = new TypeScanner();
        ts.scan(TypeScannerTest.class);
        assertEquals(0, ts.getModel().modelInterface().size());
    }
    
    @Test
    public void simpleTest() throws IllegalArgumentException, IllegalAccessException {
        TypeScanner ts = new TypeScanner();
        ts.scan(Simple.class);
        Model m = ts.getModel();
        assertEquals(1, m.modelInterface().size());
        assertEquals(3, m.modelInterface().get(Simple.class).attributes.size());
        
        Simple s = new Simple();
        TypeDetails td = m.modelInterface().get(Simple.class);
        PropertyAttribute mp = (PropertyAttribute) td.attributes.get("property");
        mp.set(s, "Hello");
        assertEquals(mp.get(s), "Hello");
        assertEquals(s.property, "Hello");
        
        mp = (PropertyAttribute) td.attributes.get("property1");
        mp.set(s, "10");
        assertEquals((int) mp.get(s), 10);
        assertEquals(s.prop1, 10);
        
        OperationAttribute op = (OperationAttribute) td.attributes.get("add");
        assertEquals(op.invoke(s, "23"), 33);
    }
    
    @Test
    public void propertySetterTest() throws IllegalArgumentException, IllegalAccessException {
        TypeScanner ts = new TypeScanner();
        ts.scan(PropertySetter.class);
        Model m = ts.getModel();
        
        PropertySetter s = new PropertySetter();
        TypeDetails td = m.modelInterface().get(PropertySetter.class);
        PropertyAttribute mp = (PropertyAttribute) td.attributes.get("property");
        mp.set(s, "Hello");
        assertEquals(mp.get(s), "Hello1");
        assertEquals(s.property, "Hello1");
    }
    
    @Test
    public void propertyGetterTest() throws IllegalArgumentException, IllegalAccessException {
        TypeScanner ts = new TypeScanner();
        ts.scan(PropertyGetter.class);
        Model m = ts.getModel();
        
        PropertyGetter s = new PropertyGetter();
        TypeDetails td = m.modelInterface().get(PropertyGetter.class);
        PropertyAttribute mp = (PropertyAttribute) td.attributes.get("property");
        mp.set(s, "Hello");
        assertEquals(mp.get(s), "Hello1");
        assertEquals(s.property, "Hello");
    }
    
    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void dualAnnotatedTest() {
        TypeScanner ts = new TypeScanner();
        ts.scan(ErrWrongAnnotated.class);
    }
    
    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void uniquePropNameTest() {
        TypeScanner ts = new TypeScanner();
        ts.scan(ErrSamePropName.class);
    }
    
    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void checkPropNameEmptyTest() {
        TypeScanner ts = new TypeScanner();
        ts.checkPropertyName("");
    }
    
    @Test
    public void checkPropNameOkTest() {
        TypeScanner ts = new TypeScanner();
        ts.checkPropertyName("1a");
    }
    
    @Test
    public void propertyNameOkTest() {
        TypeScanner ts = new TypeScanner();
        assertEquals(ts.propertyName("setab"), "ab");
        assertEquals(ts.propertyName("getb"), "b");
        assertEquals(ts.propertyName("is123"), "123");
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void propertyNameErrorTest() {
        TypeScanner ts = new TypeScanner();
        ts.propertyName("sgetab");
    }

    @Test
    public void isSetMethodTest() {
        TypeScanner ts = new TypeScanner();
        assertTrue(ts.isSetMethod("seta"));
        assertFalse(ts.isSetMethod("geta"));
        assertFalse(ts.isSetMethod("isa"));
    }
    
    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void overloadOpTest() {
        TypeScanner ts = new TypeScanner();
        ts.scan(ErrOverloadOp.class);
    }
}
