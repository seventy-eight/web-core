package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 *         Date: 24-02-13
 *         Time: 21:47
 */
public abstract class Entity extends AbstractItem {

    public Entity( MongoDocument document ) {
        super( document );
    }
}
