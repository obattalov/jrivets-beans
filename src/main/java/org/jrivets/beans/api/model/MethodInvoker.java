package org.jrivets.beans.api.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.jrivets.util.JsonSerializer;

public final class MethodInvoker {

    public static Object invoke(Object o, Method m, String... params) {
        try {
            m.setAccessible(true);
            return m.invoke(o, transalteArgs(m, params));
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new MethodInvocationException("Exception while invoking " + m + " for object " + o + " with params="
                    + params, e);
        }
    }

    public static void setField(Object o, Field f, String param) {
        f.setAccessible(true);
        try {
            f.set(o, JsonSerializer.fromJson(param, f.getType()));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new MethodInvocationException("Exception while setting field " + f + " value for object " + o + " with value="
                    + param, e);
        }
    }
    
    public static Object getField(Object o, Field f) {
        f.setAccessible(true);
        try {
            return f.get(o);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new MethodInvocationException("Exception while getting field " + f + " value for the object " + o, e);
        }
    }
    
    static Object[] transalteArgs(Method m, String... params) {
        Parameter[] mParams = m.getParameters();
        if (mParams.length != params.length) {
            throw new IllegalArgumentException("Expected " + mParams.length + ", but provided " + params.length);
        }
        
        Object[] result = new Object[mParams.length];
        for (int idx = 0; idx < mParams.length; idx++) {
            Parameter mParam = mParams[idx];
            result[idx] = JsonSerializer.fromJson(params[idx], mParam.getParameterizedType());
        }
        return result;
    }
}
