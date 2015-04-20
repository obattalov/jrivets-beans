package org.jrivets.beans.auth;

import org.jrivets.util.UID;

public interface AuthTokenAuthenticator {

    UID check(String authToken);
    
}
