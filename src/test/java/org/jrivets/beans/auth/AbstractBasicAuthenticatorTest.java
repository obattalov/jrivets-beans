package org.jrivets.beans.auth;

import org.jrivets.util.UID;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AbstractBasicAuthenticatorTest {

    @Test
    public void checkDefaultTest() {
        assertNotNull(new AbstractBasicAuthenticator() {
            @Override
            public BasicAuthInfo getByUserName(String username) {
                return username.equals("user") ? new BasicAuthInfo(new UID(1234L), "salt", "4edf07edc95b2fdcbcaf2378fd12d8ac212c2aa6e326c59c3e629be3039d6432") : null;
            }
        }.check(new Credentials("user", "test")));
    }
 
    @Test
    public void slowEqualsTest() {
        assertTrue(AbstractBasicAuthenticator.slowEquals("abc", "abc"));
        assertFalse(AbstractBasicAuthenticator.slowEquals("abc", "Abc"));
    }
    
}
