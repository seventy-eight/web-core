package org.seventyeight.web.utilities;

import org.seventyeight.web.model.RootedItem;

import java.util.HashSet;

/**
 * @author cwolfgang
 *         Date: 31-01-13
 *         Time: 23:31
 */
public class EntitySet extends HashSet<RootedItem> {

    public EntitySet applyFilter( EntitySetFilter filter ) {
        filter.filter( this );

        return this;
    }
}
