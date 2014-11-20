package org.seventyeight.web.utilities;

import com.google.gson.JsonObject;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.PersistedNode;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.model.CallContext;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author cwolfgang
 */
public class Parameters extends HashMap<String, String> implements CallContext {

    private User user;
    private PersistedNode modelObject;
    private JsonObject json = null;

    private Core core;

    public Parameters( Core core ) {
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

    @Override
    public Locale getLocale() {
        return new Locale( "da", "DK" );
    }

    @Override
    public JsonObject getJsonField() {
        if(json == null) {
            try {
                json = JsonUtils.getJsonFromRequest( this );
                if(user != null) {
                    json.addProperty( SESSION_USER, user.getIdentifier() );
                }
            } catch( JsonException e ) {
                json = new JsonObject();
            }
        }

        return json;
    }

    @Override
    public Core getCore() {
        return core;
    }
}
