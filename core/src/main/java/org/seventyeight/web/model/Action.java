package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public abstract class Action<T extends Action<T>> extends AbstractExtension<T> implements Node {

    public Action( MongoDocument document ) {
        super( document );
    }

    public abstract String getUrlName();
    public abstract String getDisplayName();
}
