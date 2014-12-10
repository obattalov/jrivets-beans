package org.jrivets.beans.auth.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class CookieUtils {

    static String getCookie(HttpServletRequest httpRequest, String cookieName) {
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies){
            if (cookieName.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
    
    static void setCookie(HttpServletResponse httpResponse, String cookieName, String cookieValue) {
        Cookie cookie = null;
        if (cookieValue == null) {
            cookie = new Cookie(cookieName, "");
            cookie.setMaxAge(0);
        } else {
            cookie = new Cookie(cookieName, cookieValue);
        }
        cookie.setPath("/");
        httpResponse.addCookie(cookie);
    }
    
    static void deleteCookie(HttpServletResponse httpResponse, String cookieName) {
        setCookie(httpResponse, cookieName, null);
    }
}
