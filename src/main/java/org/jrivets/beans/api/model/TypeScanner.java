package org.jrivets.beans.api.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jrivets.beans.Operation;
import org.jrivets.beans.Property;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

public final class TypeScanner {

    private final Logger logger = LoggerFactory.getLogger(TypeScanner.class);

    private Map<Class<?>, TypeDetails> typeDetails = new HashMap<>();

    public Model getModel() {
        return new Model(typeDetails);
    }
    
    public TypeDetails scan(Class<?> clazz) {
        TypeDetails details = typeDetails.get(clazz);
        if (details != null) {
            return details;
        }
        return scanInternally(clazz);
    }

    private TypeDetails scanInternally(Class<?> clazz) {
        TypeDetails details = typeDetails.get(clazz);
        if (details != null) {
            return details;
        }

        details = scanType(clazz);
        if (details != null) {
            typeDetails.put(clazz, details);
        }
        return details;
    }

    /**
     * Scans provided class and its super-classes also.
     */
    private TypeDetails scanType(Class<?> clazz) {
        logger.info("*** Scanning ", clazz);
        try {
            Map<String, Attribute> attributes = new HashMap<>();
            Class<?> clz = clazz;
            while (clz != null) {
                scanFields(clz, attributes);
                scanMethods(clz, attributes);
                clz = clz.getSuperclass();
            }
    
            if (attributes.size() == 0) {
                logger.warn("No interface annotations for class ", clazz);
                return null;
            }
            return new TypeDetails(attributes);
        } finally {
            logger.info("*** Done with ", clazz);
        }
    }

    private void scanFields(Class<?> clazz, Map<String, Attribute> attributes) {
        Field[] fields = clazz.getDeclaredFields();
        logger.debug("Scanning fields of ", clazz.getSimpleName());
        if (fields != null) {
            for (Field f: fields) {
                if (!f.isAnnotationPresent(Property.class)) {
                    continue;
                }
                
                logger.debug("Found field=", f.getType(), " ", f.getName(), " annotated by @Property");
                String propName = f.getName();
                PropertyAttribute mpa = createMethodProperty(attributes.get(propName), propName, clazz);
                mpa.setField(f);
                attributes.put(propName, mpa);
            }
        }
    }

    private void scanMethods(Class<?> clazz, Map<String, Attribute> attributes) {
        Method[] methods = clazz.getDeclaredMethods();
        logger.debug("Scanning methods of ", clazz.getSimpleName());
        if (methods != null) {
            for (Method m: methods) {
                boolean op = m.isAnnotationPresent(Operation.class);
                boolean prop = m.isAnnotationPresent(Property.class);

                if (op && prop) {
                    onError("Method " + m.getName() + " of " + clazz + " is annotated by both @Operation and @Property");
                }

                String name = m.getName().trim().toLowerCase();
                if (op) {
                    logger.debug("Found method m=", m.getName(), "(", Arrays.toString(m.getParameters()), ") annotated by @Operation");
                    assertUniqueName(attributes, name);
                    attributes.put(name, new OperationAttribute(clazz, m));
                }

                if (prop) {
                    logger.debug("Found method m=", m.getName(), "(", Arrays.toString(m.getParameters()), ") annotated by @Property");
                    String propName = propertyName(name);
                    PropertyAttribute mpa = createMethodProperty(attributes.get(propName), propName, clazz);
                    setPropertyMethod(mpa, m, name, propName);
                    attributes.put(propName, mpa);
                }
            }
        }
    }

    private PropertyAttribute createMethodProperty(Attribute a, String propName, Class<?> clazz) {
        if (a == null) {
            return new PropertyAttribute(clazz, propName);
        } else if (a instanceof PropertyAttribute) {
            return (PropertyAttribute) a;
        }
        onError("Attribute name uniquiness violation: there is an attribute " + a + " already defined for the name \""
                + propName + "\"");
        return null;
    }

    private void setPropertyMethod(PropertyAttribute mpa, Method m, String name, String propName) {
        if (isSetMethod(name)) {
            setSetMethod(mpa, m, propName);
            return;
        }
        setGetMethod(mpa, m, propName);
    }

    private void setSetMethod(PropertyAttribute mpa, Method m, String propName) {
        if (mpa.setMethod != null) {
            if (mpa.setMethod.getDeclaringClass().equals(m.getDeclaringClass())) {
                onError("Set method is already defined for the property \"" + propName + "\"");
            }
            logger.warn("Set method is already defined for \"", propName, "\" property in derived class.");
        }
        mpa.setMethod = m;
    }

    private void setGetMethod(PropertyAttribute mpa, Method m, String propName) {
        if (mpa.getMethod != null) {
            if (mpa.getMethod.getDeclaringClass().equals(m.getDeclaringClass())) {
                onError("Get method is already defined for the property \"" + propName + "\"");
            }
            logger.warn("Set method is already defined for \"", propName, "\" property in derived class.");
            return;
        }
        mpa.getMethod = m;
    }

    boolean isSetMethod(String mName) {
        return mName.startsWith("set");
    }

    String propertyName(String mName) {
        if (mName.startsWith("get") || mName.startsWith("set")) {
            return checkPropertyName(mName.substring(3));
        }
        if (mName.startsWith("is")) {
            return checkPropertyName(mName.substring(2));
        }
        onError("The property method name(\"" + mName + "\") should start from 'get', 'set' or 'is' words");
        return null;
    }
    
    String checkPropertyName(String pName) {
        if (pName.length() < 1) {
            onError("The property method name should start from 'get', 'set' or 'is' words and has at least one letter in its name (ex: getA(), setBc(int) etc.)");
        }
        return pName;
    }

    private void assertUniqueName(Map<String, Attribute> attributes, String name) {
        Attribute a = attributes.get(name);
        if (a != null) {
            logger.error("Attribute name uniquiness violation: there is an attribute ", a,
                    " already defined for the name ", name);
            throw new IllegalArgumentException("The name \"" + name + "\" is already defined in " + a.clazz);
        }
    }

    private void onError(String msg) {
        logger.error("Type scan cancelled, got the error: ", msg);
        throw new IllegalArgumentException(msg);
    }
}
