package org.jrivets.beans.auth.web;

import java.io.IOException;

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

import org.jrivets.beans.auth.SecurityContext;
import org.jrivets.beans.auth.SecurityContextHolder;
import org.jrivets.beans.auth.Session;
import org.jrivets.beans.auth.SessionService;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.UID;

@Singleton
public class CookieAuthFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(CookieAuthFilter.class);

    private final SessionService sessionService;

    private final String cookieName;

    @Inject
    CookieAuthFilter(SessionService sessionService, @Named("auth.cookieName") String cookieName) {
        this.sessionService = sessionService;
        this.cookieName = cookieName;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String cookie = CookieUtils.getCookie(httpRequest, cookieName);
        if (cookie != null) {
            Session session = sessionService.get(new UID(cookie));
            if (session != null) {
                logger.debug("Found session bu cookie: ", session);
                SecurityContextHolder.setContext(new SecurityContext(session));
            } else {
                logger.debug("Unset cookie ", cookieName, "=", cookie, " , no session for it.");
                CookieUtils.deleteCookie(httpResponse, cookieName);
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy() {

    }
}
