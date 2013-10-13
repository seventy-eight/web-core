package org.seventyeight.web.model;

import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public interface Authorizer {
    public enum Authorization {
        NONE,
        VIEW,
        MODERATE;

        public static Authorization get( boolean isPost ) {
            if( isPost ) {
                return MODERATE;
            } else {
                return VIEW;
            }
        }

        public boolean canModerate() {
            return this.ordinal() >= MODERATE.ordinal();
        }
    }

    public Authorization getAuthorization( User user ) throws AuthorizationException;
}
