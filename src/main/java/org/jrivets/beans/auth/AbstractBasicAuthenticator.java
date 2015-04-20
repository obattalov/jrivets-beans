package org.jrivets.beans.auth;

import java.util.function.BiFunction;

import org.apache.commons.codec.digest.DigestUtils;
import org.jrivets.util.UID;

public abstract class AbstractBasicAuthenticator implements BasicAuthenticator {

    /**
     * Represents function to generate a hash for given salt and credentials.
     * Hash result should be a HEX string(in lower case).
     */
    private final BiFunction<String, Credentials, String> hashFunction;

    protected AbstractBasicAuthenticator() {
        this((String salt, Credentials credentials) -> DigestUtils.sha256Hex(credentials.getSecret() + salt)
                .toLowerCase());
    }

    protected AbstractBasicAuthenticator(BiFunction<String, Credentials, String> hashFunction) {
        this.hashFunction = hashFunction;
    }

    /**
     * Checks credentials and provide user information in case of success, or
     * null if the user is not found or password hash doesn't match with
     * expected one.
     * 
     * @param c - credentials
     * @return entityID, if authenticated
     */
    @Override
    public UID check(Credentials c) {
        BasicAuthInfo aInfo = getByUserName(c.getLogin());
        if (aInfo == null) {
            return null;
        }
        if (slowEquals(hashFunction.apply(aInfo.getSalt(), c), aInfo.getHash())) {
            return aInfo.getEntityId();
        }
        return null;
    }

    /**
     * Returns salt and hash strings for given username. Should return null if
     * user is not found, not null result is the expected user info.
     * 
     * @param username
     * @return null if the user is not found, otherwise its salt and hash.
     */
    public abstract BasicAuthInfo getByUserName(String username);

    /**
     * Comparing the strings (hashes) in "length-constant" time ensures that an
     * attacker cannot extract the hash of a password in an on-line system using
     * a timing attack, then crack it off-line.
     * 
     * @param s1
     * @param s2
     * @return
     */
    public static boolean slowEquals(String s1, String s2) {
        byte[] a = s1.getBytes();
        byte[] b = s2.getBytes();
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
