package org.seventyeight.web.authentication;

import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

/**
 * @author cwolfgang
 */
public interface Authentication {
    public static final String __SESSION_ID = "session";
    public static final String __NAME_KEY = "username";
    public static final String __PASS_KEY = "password";
    public static final String __FORM_KEY = "login-form";
    public static final int __HOUR = 60 * 60;

    void authenticate( Request request, Response response ) throws AuthenticationException;
    public Session login( String username, String password ) throws AuthenticationException;
}
