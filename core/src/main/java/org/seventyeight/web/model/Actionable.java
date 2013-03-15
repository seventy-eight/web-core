package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 03-12-12
 *         Time: 09:56
 */
public abstract class Actionable {

    public List<Action> getActions() {
        return Collections.emptyList();
    }

    public Object getDynamic( NodeItem parent, String token ) {
        for( Action a : getActions() ) {
            if( a==null ) {
                continue;
            }

            String urlName = a.getUrlName();
            if( urlName==null ) {
                continue;
            }

            if( urlName.equals( token ) ) {
                return a;
            }
        }
        return null;
    }
}
