package org.seventyeight.web.authorization;

import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public interface Ownable {
    public boolean isOwner( User user );
    public User getOwner() throws ItemInstantiationException, NotFoundException;
}
