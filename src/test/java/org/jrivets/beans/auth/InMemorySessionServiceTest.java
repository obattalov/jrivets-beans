package org.jrivets.beans.auth;

import org.jrivets.util.SyncUtils;
import org.jrivets.util.UID;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class InMemorySessionServiceTest {

    @Test
    public void putGetTest() {
        InMemorySessionService ss = new InMemorySessionService(100000L);
        UID uid = UID.randomUID();
        Session s = ss.createNew(new BasicAuthInfo(uid, "salt", "hash"));
        assertNotEquals(s.getId(), uid);
        assertEquals(s.entityId, uid);
        Session s2 = ss.get(s.getId());
        assertEquals(s.getId(), s2.getId());
        assertEquals(s.entityId, s2.entityId);
    }
    
    @Test
    public void expirationTest() {
        InMemorySessionService ss = new InMemorySessionService(1L);
        UID uid = UID.randomUID();
        Session s = ss.createNew(new BasicAuthInfo(uid, "salt", "hash"));
        assertNotEquals(s.getId(), uid);
        assertEquals(s.entityId, uid);
        SyncUtils.sleepQuietly(1010L);
        assertNull(ss.get(s.getId()));
    }
}
