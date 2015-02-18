package org.jrivets.beans.auth;

import org.jrivets.util.UID;

public interface SessionService {

    Session get(UID sessId);

    Session createNew(BasicAuthInfo aInfo);
}
