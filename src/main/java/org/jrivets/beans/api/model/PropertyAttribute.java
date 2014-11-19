package org.jrivets.beans.api.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyAttribute extends Attribute {

    Method getMethod;

    Method setMethod;

    Field field;

    final String name;

    PropertyAttribute(Class<?> clazz, String name) {
        super(clazz);
        this.name = name;
    }

    void setField(Field field) {
        if (this.field != null) {
            throw new IllegalArgumentException("The field already marked like property with this name: " + this.field);
        }
        this.field = field;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object o) {
        if (getMethod != null) {
            return (T) MethodInvoker.invoke(o, getMethod);
        }
        return (T) MethodInvoker.getField(o, field);
    }

    public void set(Object o, String value) {
        if (setMethod != null) {
            MethodInvoker.invoke(o, setMethod, value);
            return;
        }
        MethodInvoker.setField(o, field, value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("PropertyAttribute: {name=").append(name).append(", field=")
                .append(field != null ? field.getName() : "null").append(", getMethod=")
                .append(getMethod != null ? getMethod.getName() : "null").append(", setMethod=")
                .append(setMethod != null ? setMethod.getName() : "null").append(", clazz=").append(clazz.getName())
                .append("}").toString();
    }
}
