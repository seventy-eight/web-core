package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public abstract class Action<T extends Action<T>> extends AbstractExtension<T> implements Node {

    private Node parent;

    public Action( Node parent ) {
        super( parent );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    public abstract String getUrlName();
    public abstract String getDisplayName();
}
