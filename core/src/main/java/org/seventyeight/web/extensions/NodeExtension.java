package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public abstract class NodeExtension<T extends NodeExtension<T>> extends AbstractExtension<T> {

    public NodeExtension( Core core, Node node, MongoDocument document ) {
        super( core, node, document );
    }

    public abstract static class NodeExtensionDescriptor<T extends NodeExtension<T>> extends ExtensionDescriptor<T> {

        protected NodeExtensionDescriptor( Core core ) {
            super();
        }
        
        

        @Override
        public Class<NodeExtension> getExtensionClass() {
            return NodeExtension.class;
        }

        @Override
        public boolean canHaveMultiple() {
            return true;
        }

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( NodeExtension.class, "NodeExtensions", canHaveMultiple() );
        }

    }
}
