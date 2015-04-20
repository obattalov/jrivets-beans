package org.jrivets.beans.auth.web;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrivets.beans.auth.AuthTokenAuthenticator;
import org.jrivets.beans.auth.SecurityContextHolder;
import org.jrivets.beans.auth.Session;
import org.jrivets.beans.auth.SessionService;
import org.jrivets.beans.web.Constant;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.UID;

import com.google.inject.Singleton;

@Singleton
public class BearerAuthFilter extends AuthFilter {

    private final SessionService sessionService;

    private final AuthTokenAuthenticator atAuthenticator;
    
    private final String cookieName;

    @Inject
    BearerAuthFilter(SessionService sessionService, AuthTokenAuthenticator atAuthenticator,
            @Named("auth.cookieName") String cookieName, @Named("auth.methods") String supportedMethods) {
        super(supportedMethods);
        this.logger = LoggerFactory.getLogger(BearerAuthFilter.class);
        this.sessionService = sessionService;
        this.atAuthenticator = atAuthenticator;
        this.cookieName = cookieName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader(Constant.AUTHORIZATION_HEADER);

        if (authHeader != null) {
            ErrorCode errorCode = doBearerAuthentication(authHeader, httpResponse);
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
    }

    private ErrorCode doBearerAuthentication(String authHeader, HttpServletResponse httpResponse) {
        StringTokenizer st = new StringTokenizer(authHeader);
        if (!st.hasMoreTokens()) {
            return ErrorCode.UNKNOWN_AUTH_TYPE;
        }

        String method = st.nextToken();
        if (!method.equalsIgnoreCase("bearer")) {
            return null;
        }

        String authToken = new String(st.nextToken());
        UID entityId = atAuthenticator.check(authToken);
        if (entityId == null) {
            return ErrorCode.BAD_CREDENTIALS;
        }

        Session session = sessionService.createNew(entityId);
        SecurityContextHolder.getContext().setSession(session);
        CookieUtils.setCookie(httpResponse, cookieName, session.getId().toString());
        return null;
    }

}
