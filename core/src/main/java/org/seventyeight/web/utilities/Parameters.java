package org.seventyeight.web.utilities;

import org.seventyeight.web.User;
import org.seventyeight.web.model.AbstractItem;
import org.seventyeight.web.model.CoreRequest;

import java.util.HashMap;

/**
 * @author cwolfgang
 *         Date: 30-11-12
 *         Time: 23:23
 */
public class Parameters extends HashMap<String, String> implements CoreRequest {

    private User user;
    private AbstractItem item;

    @Override
    public AbstractItem getItem() {
        return item;
    }

    @Override
    public void setItem( AbstractItem item ) {
        this.item = item;
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
}
