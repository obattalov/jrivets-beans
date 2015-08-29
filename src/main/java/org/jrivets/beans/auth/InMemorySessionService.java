package org.jrivets.beans.auth;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.UID;
import org.jrivets.util.container.AbstractKeyValueHolder;

import com.google.inject.Singleton;

@Singleton
public final class InMemorySessionService implements SessionService {
    
    private final static long DEFAULT_SESSION_TIMEOUT = TimeUnit.MINUTES.toMillis(30);

    private final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private final InMemStorage storage;

    class InMemStorage extends AbstractKeyValueHolder<UID, Session> {

        private Session newSession;
        
        InMemStorage(long expirationTimeoutMs) {
            super(expirationTimeoutMs, Integer.MAX_VALUE, true);
        }

        synchronized void put(Session session) {
            if (holders.remove(session.getId()) != null) {
                logger.warn("Oops, looks like the session was already stored ", session);
            }
            try {
                newSession = session;
                if (getValue(session.getId()) != session) {
                    logger.error("Something goes wrong - cannot store session into the storage");
                    throw new IllegalStateException("Cannot store session into InMemoryStorage");
                }
            } finally {
                newSession = null;
            }
        }
        
        @Override
        protected Session getNewValue(UID key) {
            return newSession;
        }
    }
    
    @Inject
    InMemorySessionService(@Named("auth.sessionTimeoutSec") long sessionTimeout) {
        long expirationTimeoutMs = sessionTimeout <= 0L ? DEFAULT_SESSION_TIMEOUT : TimeUnit.SECONDS
                .toMillis(sessionTimeout);
        storage = new InMemStorage(expirationTimeoutMs);
    }

    @Override
    public Session get(UID sessId) {
        return storage.getValue(sessId);
    }

    @Override
    public Session createNew(UID entityId) {
        Session s = new Session(entityId);
        storage.put(s);
        return s;
    }

    @Override
    public boolean delete(UID sessId) {
        return storage.drop(sessId);
    }
}
