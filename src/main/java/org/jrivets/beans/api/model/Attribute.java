package org.jrivets.beans.api.model;

public abstract class Attribute {

    final Class<?> clazz;
    
    Attribute(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public abstract String getName();
    
}
