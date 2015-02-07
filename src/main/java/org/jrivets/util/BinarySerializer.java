package org.jrivets.util;

import com.esotericsoftware.kryo.Kryo;

public final class BinarySerializer extends StaticSingleton {

    private static Kryo kryo = new Kryo();
    
    public static <T> T copy(T t) {
        return kryo.copy(t);
    }
    
}
