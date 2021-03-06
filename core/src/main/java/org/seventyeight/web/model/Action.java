package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.ExtensionGroup;

/**
 * @author cwolfgang
 */
public abstract class Action<T extends Action<T>> extends AbstractExtension<T> implements Node {
	
	private static Logger logger = LogManager.getLogger(Action.class);

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
            super();
        }
        
        /**
         * Returns the extension name for the url sub path
         */
        public abstract String getExtensionName();

        @Override
        public ExtensionGroup getExtensionGroup() {
        	logger.debug("Getting extionsion group class, {}", getClazz());
            return new ExtensionGroup( getClazz(), "Action", true );
        }
    }
}
