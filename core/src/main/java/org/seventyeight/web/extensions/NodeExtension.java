package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public abstract class NodeExtension<T extends NodeExtension<T>> extends AbstractExtension<T> {

    public NodeExtension( Node node, MongoDocument document ) {
        super( node, document );
    }

    public abstract static class NodeExtensionDescriptor<T extends NodeExtension<T>> extends ExtensionDescriptor<T> {

        @Override
        public Class<NodeExtension> getExtensionClass() {
            return NodeExtension.class;
        }

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( NodeExtension.class, "NodeExtensions", true );
        }

    }
}
