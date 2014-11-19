package org.jrivets.beans.api.model;

import org.jrivets.util.JsonSerializer;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PropertyAttributeTest {

    public int fieldInt;
    
    public ComplexField complexField;
    
    private static class ComplexField {
        String abc;
        int i;
    }
    
    @Test
    public void simpleFieldTest() throws NoSuchFieldException, SecurityException {
        PropertyAttribute pa = new PropertyAttribute(PropertyAttributeTest.class, "simple");
        assertEquals(pa.getName(), "simple");
        pa.setField(PropertyAttributeTest.class.getField("fieldInt"));
        pa.set(this, "123");
        assertEquals((int) pa.get(this), 123);
    }
    
    @Test
    public void complexFieldTest() throws NoSuchFieldException, SecurityException {
        PropertyAttribute pa = new PropertyAttribute(PropertyAttributeTest.class, "complex");
        pa.setField(PropertyAttributeTest.class.getField("complexField"));
        assertNull(pa.get(this));
        
        complexField = new ComplexField();
        complexField.abc = "Hello";
        complexField.i = 1234;
        
        ComplexField cf = (ComplexField) pa.get(this);
        assertEquals(cf.abc, complexField.abc);
        assertEquals(cf.i, complexField.i);
        
        cf.abc = "H";
        cf.i = 345;
        pa.set(this, JsonSerializer.toJson(cf));
        assertEquals(cf.abc, complexField.abc);
        assertEquals(cf.i, complexField.i);
    }
}
