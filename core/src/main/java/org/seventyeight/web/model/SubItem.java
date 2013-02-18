package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 17:22
 */
public abstract class SubItem implements Item {

    protected MongoDocument document;

    public SubItem( MongoDocument document ) {
        this.document = document;
    }
}
