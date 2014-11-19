package org.jrivets.util;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class JsonSerializerTest {

    private static class StaticA implements I {
        
        private final int i;
        
        private final String s;
        
        StaticA(int i, String s) {
            this.i = i;
            this.s = s;
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof StaticA)) {
                return false;
            }
            StaticA a = (StaticA) o;
            return a.i == i && (s == null ? a.s == null : s.equals(a.s));
        }
        
        @Override 
        public int hashCode() {
            return i + (s == null ? 0 : s.hashCode());
        }
    }
    
    @Test
    public void nullTest() {
        StaticA a = null;
        check(a);
    }
    
    @Test
    public void staticTest() {
        check(new StaticA(1, "23"));
    }

    @Test
    public void nullTestEx() {
        checkEx(null);
    }
    
    @Test
    public void fromNullEx() {
        assertNull(JsonSerializer.fromJsonWithTypeInfo(null, I.class));
    }
    
    @Test
    public void fromEmptyStringEx() {
        assertNull(JsonSerializer.fromJsonWithTypeInfo("", I.class));
    }
    
    @Test
    public void fromNullStringEx() {
        assertNull(JsonSerializer.fromJsonWithTypeInfo("null", I.class));
    }
    
    @Test
    public void staticClassEx() {
        checkEx(new StaticA(1234, "rtyer"));
    }
    
    @Test
    public void typeTest() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(0);
        list.add(1);
        list.add(2);
        checkType(list, new TypeToken<ArrayList<Integer>>(){}.getType());
    }

    @Test
    public void simpleClassEx() {
        checkEx(new A(2345, "qwert"));
    }

    @Test
    public void extendedClassEx() {
        checkEx(new A(3456, "asdfads"));
    }
    
    @Test
    public void deserializationEx() {
        A a = new A(3456123, "123asdfads");
        String jsonObject = JsonSerializer.toJsonWithTypeInfo(a);
        A deserializedObject = JsonSerializer.fromJsonWithTypeInfo(jsonObject, A.class); 
        assertEquals(a, deserializedObject);
    }
    
    @Test
    public void objectAsSuperClas() {
        A a = new A(3612, "sdf123asdfads");
        String jsonObject = JsonSerializer.toJsonWithTypeInfo(a);
        Object deserializedObject = JsonSerializer.fromJsonWithTypeInfo(jsonObject, Object.class); 
        assertEquals(a, deserializedObject);
    }
    
    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void invalidInput() {
        String jsonObject = JsonSerializer.toJson(new A(3456, "asdfads"));
        JsonSerializer.fromJsonWithTypeInfo(jsonObject, I.class);
    }
    
    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void invalidInput2() {
        String jsonObject = JsonSerializer.toJsonWithTypeInfo(new A(3456, "asdfads"));
        jsonObject = jsonObject.replaceAll("\\.A", ".AAA");
        JsonSerializer.fromJsonWithTypeInfo(jsonObject, I.class);
    }
    
    @Test
    public void parseObjectsTest() {
        String[] objects = JsonSerializer.fromJson("[1234, \"ads\", '{\"abc\":123, \"def\":\"str\"}']", String[].class);
        assertEquals(objects[0], "1234");
        assertEquals(objects[1], "ads");
        assertEquals(objects[2], "{\"abc\":123, \"def\":\"str\"}");
    }
   
    @SuppressWarnings("unchecked")
    private <T> void check(T t) {
        String jsonObject = JsonSerializer.toJson(t);
        if (t == null) {
            Object o = JsonSerializer.fromJson(jsonObject, Object.class);
            assertNull(o);
            return;
        }
        T deserializedObject = JsonSerializer.fromJson(jsonObject, (Class<T>) t.getClass()); 
        assertEquals(t, deserializedObject);
    }

    private <T> void checkType(Object obj, Type objType) {
        String jsonObject = JsonSerializer.toJson(obj, objType);
        if (obj == null) {
            Object o = JsonSerializer.fromJson(jsonObject, Object.class);
            assertNull(o);
            return;
        }
        T deserializedObject = JsonSerializer.fromJson(jsonObject, objType);
        assertEquals(obj, deserializedObject);
    }

    private void checkEx(I value) {
        String jsonObject = JsonSerializer.toJsonWithTypeInfo(value);
        if (value == null) {
            I o = JsonSerializer.fromJsonWithTypeInfo(jsonObject, I.class);
            assertNull(o);
            return;
        }
        I deserializedObject = JsonSerializer.fromJsonWithTypeInfo(jsonObject, I.class); 
        assertEquals(value, deserializedObject);
    }
}

interface I {
    
}

class A implements I {
    
    private final int i;
    
    private final String s;
    
    A(int i, String s) {
        this.i = i;
        this.s = s;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof A)) {
            return false;
        }
        A a = (A) o;
        return a.i == i && (s == null ? a.s == null : s.equals(a.s));
    }
    
    @Override 
    public int hashCode() {
        return i + (s == null ? 0 : s.hashCode());
    }
}

class B extends A {
    
    B(int i, String s) {
        super(i, s);
    }
}