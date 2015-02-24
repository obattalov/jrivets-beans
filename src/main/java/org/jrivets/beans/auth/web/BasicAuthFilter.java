package org.jrivets.beans.auth.web;

import org.apache.commons.codec.binary.Base64;
import org.jrivets.beans.auth.BasicAuthInfo;
import org.jrivets.beans.auth.BasicAuthenticator;
import org.jrivets.beans.auth.Credentials;
import org.jrivets.beans.auth.SecurityContextHolder;
import org.jrivets.beans.auth.Session;
import org.jrivets.beans.auth.SessionService;
import org.jrivets.beans.web.Constant;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

@Singleton
public class BasicAuthFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(BasicAuthFilter.class);

    private String credentialsCharset = "UTF-8";

    private final SessionService sessionService;

    private final BasicAuthenticator basicAuthenticator;

    private final String cookieName;

    private final String basicRealm;

    enum ErrorCode {
        AUTH_REQUIRED("Authentication required"), //
        UNKNOWN_AUTH_TYPE("Unknown authentication type"), //
        UNSUPPORTED_AUTH_METHOD("Unsupported authentication method"), //
        INVALID_AUTH_TOKEN("Invalid authentication token"), //
        BAD_CREDENTIALS("Bad credentials");

        final String message;

        ErrorCode(String message) {
            this.message = message;
        }

    }

    @Inject
    BasicAuthFilter(SessionService sessionService, BasicAuthenticator basicAuthenticator,
            @Named("auth.cookieName") String cookieName, @Named("auth.basicRealm") String basicRealm) {
        this.sessionService = sessionService;
        this.basicAuthenticator = basicAuthenticator;
        this.cookieName = cookieName;
        this.basicRealm = basicRealm;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader(Constant.AUTHORIZATION_HEADER);

        if (authHeader != null) {
            ErrorCode errorCode = doBasicAuthentication(authHeader, httpResponse);
            if (errorCode != null) {
                unauthenticated(httpResponse, errorCode);
                return;
            }
        }

        // at this point the call can be authenticated, or completely not (no
        // authentication header and session == null), so we propagate the
        // request to other filters and it is now matter of authorization to
        // perform it or reject with the settings
        chain.doFilter(request, response);
        
        if (SecurityContextHolder.getContext().isAuthRequired()) {
            unauthenticated(httpResponse, ErrorCode.AUTH_REQUIRED);
        }
    }

    public void destroy() {

    }

    private ErrorCode doBasicAuthentication(String authHeader, HttpServletResponse httpResponse)
            throws UnsupportedEncodingException {
        StringTokenizer st = new StringTokenizer(authHeader);
        if (!st.hasMoreTokens()) {
            return ErrorCode.UNKNOWN_AUTH_TYPE;
        }

        String method = st.nextToken();
        if (!method.equalsIgnoreCase("basic")) {
            return ErrorCode.UNSUPPORTED_AUTH_METHOD;
        }

        String cred = new String(Base64.decodeBase64(st.nextToken()), credentialsCharset);
        int delim = cred.indexOf(":");
        if (delim == -1) {
            return ErrorCode.INVALID_AUTH_TOKEN;
        }

        Credentials c = new Credentials(cred.substring(0, delim).trim(), cred.substring(delim + 1).trim());
        BasicAuthInfo aInfo = basicAuthenticator.check(c);
        if (aInfo == null) {
            return ErrorCode.BAD_CREDENTIALS;
        }

        Session session = sessionService.createNew(aInfo);
        SecurityContextHolder.getContext().setSession(session);
        CookieUtils.setCookie(httpResponse, cookieName, session.getId().toString());
        return null;
    }

    protected String getErrorMessage(ErrorCode errorCode) {
        return errorCode.message;
    }

    private void unauthenticated(HttpServletResponse httpResponse, ErrorCode errorCode) throws IOException {
        logger.debug("Unathorized with the message: ", errorCode.message);
        httpResponse.setHeader(Constant.AUTHENTICATE_HEADER, "Basic realm=\"" + basicRealm + "\"");
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, getErrorMessage(errorCode));
    }

}
