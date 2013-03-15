package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;

/**
 * @author cwolfgang
 *         Date: 06-03-13
 *         Time: 19:50
 */
public abstract class NodeExtension extends AbstractExtension {

    public NodeExtension( MongoDocument document ) {
        super( document );
    }
}
