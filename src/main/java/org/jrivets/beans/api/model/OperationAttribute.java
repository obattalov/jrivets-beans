package org.jrivets.beans.api.model;

import java.lang.reflect.Method;

public class OperationAttribute extends Attribute {

    final Method method;

    OperationAttribute(Class<?> clazz, Method method) {
        super(clazz);
        this.method = method;
    }

    public Object invoke(Object o, String... params) {
        return MethodInvoker.invoke(o, method, params);
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("OperationAttribute: {method=")
                .append(method != null ? method.getName() : "null").append(", clazz=").append(clazz.getName())
                .append("}").toString();
    }
}
