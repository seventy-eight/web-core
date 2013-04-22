package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;

/**
 * @author cwolfgang
 */
public abstract class NodeExtension<T extends NodeExtension<T>> extends AbstractExtension<T> {

    public NodeExtension( MongoDocument document ) {
        super( document );
    }
}
