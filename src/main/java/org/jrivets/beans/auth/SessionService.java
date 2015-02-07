package org.jrivets.beans.auth;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jrivets.kvstorage.VersionedValue;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.UID;

@Singleton
public final class SessionService {

    private final static long DEFAULT_SESSION_TIMEOUT = TimeUnit.MINUTES.toMillis(30);

    private final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private final SessionsStorage sessionsStorage;

    private final long expirationTimeoutMs;

    @Inject
    public SessionService(SessionsStorage sessionsStorage, @Named("auth.sessionTimeout") long sessionTimeout) {
        this.sessionsStorage = sessionsStorage;
        this.expirationTimeoutMs = sessionTimeout <= 0L ? DEFAULT_SESSION_TIMEOUT : TimeUnit.SECONDS
                .toMillis(sessionTimeout);
    }

    public Session get(UID sessId) {
        VersionedValue<Session> vv = sessionsStorage.get(sessId);
        if (vv == null) {
            logger.warn("Session ", sessId, " is not found, expired?");
            return null;
        }

        Session s = vv.getValue();
        long now = System.currentTimeMillis();
        if (s.getExpirationTime() < now) {
            logger.info("Session ", sessId, " expired, delete it from the storage");
            sessionsStorage.remove(sessId);
            return null;
        }
        s.setLastTouchTime(now);
        // Do CAS, not PUT, because it can disappear and we would not like to
        // replace it this case
        return sessionsStorage.cas(s.getId(), s, vv.getVersion()) > 0 ? s : null;
    }

    public Session createNew(BasicAuthInfo aInfo) {
        Session s = new Session(aInfo.getEntityId(), expirationTimeoutMs, System.currentTimeMillis());
        sessionsStorage.put(s.getId(), s);
        return s;
    }
}
