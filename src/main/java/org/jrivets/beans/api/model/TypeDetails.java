package org.jrivets.beans.api.model;

import java.util.Collections;
import java.util.Map;

public final class TypeDetails {

    final Map<String, Attribute> attributes;
    
    TypeDetails(Map<String, Attribute> attributes) {
        this.attributes = Collections.unmodifiableMap(attributes);
    }
    
    public Map<String, Attribute> getInterfaceAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "{attributes=" + attributes + "}";
    }
    
}
