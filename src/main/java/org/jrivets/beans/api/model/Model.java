package org.jrivets.beans.api.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private final Map<Class<?>, TypeDetails> typeDetails;
    
    Model(Map<Class<?>, TypeDetails> typeDetails) {
        this.typeDetails = Collections.unmodifiableMap(new HashMap<>(typeDetails));
    }
    
    public Map<Class<?>, TypeDetails> modelInterface() {
        return typeDetails;
    }
    
}
