package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ExtensionGroup;

/**
 * @author cwolfgang
 */
public abstract class Action<T extends Action<T>> extends AbstractExtension<T> implements Node {

    public Action( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    public abstract String getDisplayName();

    /**
     * The {@link org.seventyeight.web.model.AbstractExtension.ExtensionDescriptor#getExtensionName()} will serve as the url sub space as well.
     * @param <T>
     */
    public static abstract class ActionDescriptor<T extends Action<T>> extends ExtensionDescriptor<T> {

        protected ActionDescriptor( Core core ) {
            super( core );
        }

        /*
        @Override
        public Class<T> getExtensionClass() {
            return Action.class;
        }
        */

        /*
        @Override
        public boolean isOmnipresent() {
            return true;
        }
        */

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( getClazz(), "Action", true );
        }
    }
}
