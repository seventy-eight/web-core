package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class Actionable {

    public List<Action> getActions() {
        return Collections.emptyList();
    }

    /**
     * Return a dynamic object, null if nothing applies
     * @param token
     * @return
     */
    public Object getDynamic( String token ) {
        for( Action a : getActions() ) {
            if( a == null ) {
                continue;
            }

            String urlName = a.getUrlName();
            if( urlName == null ) {
                continue;
            }

            if( urlName.equals( token ) ) {
                return a;
            }
        }

        return null;
    }
}
