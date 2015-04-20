package org.jrivets.beans.auth;

import org.jrivets.util.UID;

public interface BasicAuthenticator {

    UID check(Credentials c);
}
