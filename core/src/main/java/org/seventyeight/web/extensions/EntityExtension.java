package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;

/**
 * @author cwolfgang
 *         Date: 05-03-13
 *         Time: 23:05
 */
public abstract class EntityExtension extends NodeExtension {

    public EntityExtension( MongoDocument document ) {
        super( document );
    }
}
