package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.ExtensionGroup;

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

    public abstract String getDisplayName();

    /**
     * The {@link org.seventyeight.web.model.AbstractExtension.ExtensionDescriptor#getExtensionName()} will serve as the url sub space as well.
     * @param <T>
     */
    public static abstract class ActionDescriptor<T extends Action<T>> extends ExtensionDescriptor<T> {
        @Override
        public final String getTypeName() {
            return "action";
        }

        /*
        @Override
        public Class<T> getExtensionClass() {
            return Action.class;
        }
        */

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( getClazz(), "Action", true );
        }
    }
}
