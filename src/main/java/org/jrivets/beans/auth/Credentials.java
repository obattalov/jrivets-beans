package org.jrivets.beans.auth;

public class Credentials {

    private final String login;
    
    private final String secret;

    public Credentials(String login, String secret) {
        this.login = login;
        this.secret = secret;
    }

    public String getLogin() {
        return login;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "{login=" + login + ", secret=" + (secret == null ? "null" : "*********") + "}";
    }
    
}
