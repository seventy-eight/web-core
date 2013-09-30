package org.seventyeight.web.extensions;

import org.seventyeight.web.model.Documented;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public interface UserPortrait extends Documented {
    public String getUrl();
    public void setUser( User user );
}
