package org.seventyeight.web.authorization;

import org.seventyeight.web.model.Node;
import org.seventyeight.web.nodes.User;

/**
 * A class that defines an {@link Authorizable} entity, that determines whether or not a {@link User} is a member.
 *
 * @author cwolfgang
 */
public interface Authorizable extends Node {
    public boolean isMember( User user );
}
