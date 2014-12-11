package org.jrivets.beans.auth;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jrivets.kvstorage.InMemoryKeyValueStorage;
import org.jrivets.util.UID;

public final class InMemorySessionStorage  implements SessionsStorage {
    
    private final InMemoryKeyValueStorage<UID, Session> storage;

    @Inject
    InMemorySessionStorage(@Named("auth.sessionTimeout") long sessionTimeout) {
        storage = new InMemoryKeyValueStorage<>("AuthSess", Integer.MAX_VALUE, TimeUnit.SECONDS.toMillis(sessionTimeout*2));
    }
    
    @Override
    public void put(UID key, Session value) {
        storage.put(key, value);
    }

    @Override
    public boolean cas(UID key, Session expected, Session value) {
        return storage.cas(key, expected, value);
    }

    @Override
    public Session get(UID key) {
        return storage.get(key);
    }

    @Override
    public Session remove(UID key) {
        return storage.remove(key);
    }

}
