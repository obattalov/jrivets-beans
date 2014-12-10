package org.jrivets.beans.auth;

public interface BasicAuthenticator {

    BasicAuthInfo check(Credentials c);
}
