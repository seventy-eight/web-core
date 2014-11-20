package org.seventyeight.web.model;

import com.google.gson.JsonObject;

import org.seventyeight.utils.StopWatch;
import org.seventyeight.web.Core;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Response;

/**
 * @author cwolfgang
 */
public interface CallContext {
    public static final String SESSION_USER = "sessionUser";
	
    public enum MethodType {
        GET,
        POST,
        DELETE,
        PUT
    }
    
    public MethodType getMethodType();
    
    public Class<? extends CallContext> getRequestClass();
    public Class<? extends Response> getResponseClass();
    
    public String getUri();
	public User getUser();
    public void setUser( User user );
    public JsonObject getJson();
    public Core getCore();
    
    public void setStopWatch(StopWatch stopWatch);

    public StopWatch getStopWatch();
}
