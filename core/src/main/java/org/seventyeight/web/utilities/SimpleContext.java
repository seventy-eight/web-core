package org.seventyeight.web.utilities;

import com.google.gson.JsonObject;

import org.seventyeight.utils.StopWatch;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.PersistedNode;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.model.CallContext;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author cwolfgang
 */
public class SimpleContext implements CallContext {

    private User user;
    private JsonObject json = new JsonObject();

    private Core core;

    private String uri;
    
    private StopWatch stopWatch = new StopWatch();
    
    public SimpleContext( Core core ) {
        this.core = core;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser( User user ) {
        this.user = user;
    }

    public void setUri(String uri) {
    	this.uri = uri;
    }
    
    /*
    @Override
    public String getParameter( String key ) {
        return this.get( key );
    }

    @Override
    public String[] getParameterValues( String key ) {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T getValue( String key ) {
        return (T) this.get( key );
    }

    @Override
    public <T> T getValue( String key, T defaultValue ) {
        if( containsKey( key ) ) {
            return (T) get( key );
        } else {
            return defaultValue;
        }
    }

    @Override
    public Integer getInteger( String key ) {
        return getInteger( key, null );
    }

    @Override
    public Integer getInteger( String key, Integer defaultValue ) {
        if( containsKey( key ) ) {
            return Integer.parseInt( get( key ) );
        } else {
            return defaultValue;
        }
    }
	*/
    
    @Override
    public JsonObject getJson() {
        return json;
    }

    @Override
    public Core getCore() {
        return core;
    }

	@Override
	public MethodType getMethodType() {
		return null;
	}

	@Override
	public Class<? extends CallContext> getRequestClass() {
		return SimpleContext.class;
	}

	@Override
	public Class<? extends Response> getResponseClass() {
		return Response.class;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
    public void setStopWatch( StopWatch stopWatch ) {
        this.stopWatch = stopWatch;
    }

	@Override
    public StopWatch getStopWatch() {
        return stopWatch;
    }
}
