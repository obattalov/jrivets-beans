package org.jrivets.beans.auth.web;

enum ErrorCode {
    AUTH_REQUIRED("Authentication required"), //
    UNKNOWN_AUTH_TYPE("Unknown authentication type"), //
    UNSUPPORTED_AUTH_METHOD("Unsupported authentication method"), //
    INVALID_BASIC_AUTH_TOKEN("Invalid basic authentication token"), //
    BAD_CREDENTIALS("Bad credentials");

    final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
