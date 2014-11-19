package org.jrivets.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The {@code JsonSerializer} allows to transform a POJO to JSON back and forth.
 * <p>
 * The implementation is just a simple wrapper around {@link Gson} object and
 * provides static methods for (de-)serialization. It also allows to add
 * meta-information to JSON form while serializing a POJO with a purpose to find
 * the correct POJO's type on de-serialization phase. The functionality can be
 * helpful for serializing a hierarchy of objects without knowing concrete
 * POJO's type at (de-)serialization moment.
 * 
 * @author Dmitry Spasibenko 
 * 
 */
public final class JsonSerializer {

    private final Gson gson;

    private static class SerializerHolder {
        private static JsonSerializer serializer = new JsonSerializer();
    }

    private static class Holder {

        private final String typeName;

        private final String object;

        Holder(String typeName, String object) {
            this.typeName = typeName;
            this.object = object;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getObject() {
            return object;
        }

    }

    private JsonSerializer() {
        GsonBuilder gsonBilder = new GsonBuilder();
        this.gson = gsonBilder.create();
    }

    /**
     * Returns JSON representation of {@code t}.
     * 
     * @param t - a POJO to be serialized
     * @return JSON representation of t
     */
    public static <T> String toJson(T t) {
        return SerializerHolder.serializer.gson.toJson(t);
    }

    public static String toJson(Object obj, Type objType) {
        return SerializerHolder.serializer.gson.toJson(obj, objType);
    }

    /**
     * Converts a JSON to POJO of class clazz.
     * 
     * @param jsonObject
     *            - a JSON representation of a clazz object.
     * @param clazz
     *            - POJO's type
     * @return POJO for the JSON
     */
    public static <T> T fromJson(String jsonObject, Class<T> clazz) {
        return SerializerHolder.serializer.gson.fromJson(jsonObject, clazz);
    }

    public static <T> T fromJson(String jsonObject, Type objType) {
        return SerializerHolder.serializer.gson.fromJson(jsonObject, objType);
    }

    /**
     * Returns JSON representation of {@code t} with type information of the
     * serialized object. The type information can be used on de-serialization
     * phase if the invoker doesn't know the exact type of the object.
     * 
     * @param t
     *            - POJO to be serialized
     * @return JSON form of {@code t}
     */
    public static <T> String toJsonWithTypeInfo(T t) {
        return SerializerHolder.serializer.toJsonExt(t);
    }

    /**
     * Returns POJO for provided JSON object.
     * 
     * @param jsonObject
     *            - an object JSON form.
     * @param superClass
     *            - one of super classes of the result POJO
     * @return POJO for the JSON
     */
    public static <T> T fromJsonWithTypeInfo(String jsonObject, Class<T> superClass) {
        try {
            return SerializerHolder.serializer.fromJsonExt(jsonObject);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot deserialize " + jsonObject, e);
        }
    }

    private String toJsonExt(Object object) {
        String jsonObject = gson.toJson(object);
        if (object != null) {
            jsonObject = gson.toJson(new Holder(object.getClass().getName(), jsonObject));
        }
        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    private <T> T fromJsonExt(String jsonObject) throws ClassNotFoundException {
        Holder holder = gson.fromJson(jsonObject, Holder.class);
        if (holder == null) {
            return null;
        }
        if (Strings.isNullOrEmpty(holder.getTypeName())) {
            throw new IllegalArgumentException("Cannot deserialize because no type info: " + jsonObject);
        }
        return (T) gson.fromJson(holder.getObject(), Class.forName(holder.getTypeName()));
    }
}
