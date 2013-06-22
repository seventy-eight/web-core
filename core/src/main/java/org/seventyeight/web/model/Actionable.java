package org.seventyeight.web.model;

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
     * Return a single {@link Action}, null if nothing applies
     * @param token
     * @return
     */
    public Action getAction( String token ) {
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
