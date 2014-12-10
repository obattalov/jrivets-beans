package org.jrivets.beans.web;

import javax.ws.rs.core.MediaType;

public final class Constant {
    
    public final static String CHARSET_UTF8 = "charset=utf-8";

    public final static String MEDIA_TYPE_APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON + ";" + CHARSET_UTF8;

    public final static String MEDIA_TYPE_APPLICATION_OCTET_STREAM_UTF8 = MediaType.APPLICATION_OCTET_STREAM + ";"
            + CHARSET_UTF8;

    public final static String MEDIA_TYPE_TEXT_PLAIN_UTF8 = MediaType.TEXT_PLAIN + ";" + CHARSET_UTF8;
    
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String AUTHENTICATE_HEADER = "WWW-Authenticate";
}