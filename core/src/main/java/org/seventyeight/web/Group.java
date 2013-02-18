package org.seventyeight.web;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractItem;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 22:45
 */
public class Group extends AbstractItem {

    public Group( MongoDocument document ) {
        super( document );
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
