package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public abstract class Action<T extends Action<T>> extends AbstractExtension<T> implements Node {

    public Action( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    public abstract String getUrlName();
    public abstract String getDisplayName();

    public static abstract class ActionDescriptor<T extends Action<T>> extends ExtensionDescriptor<T> {
        @Override
        public final String getTypeName() {
            return "action";
        }
    }
}
