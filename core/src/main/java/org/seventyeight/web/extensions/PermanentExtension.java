package org.seventyeight.web.extensions;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;

/**
 * @author cwolfgang
 */
public abstract class PermanentExtension<T extends PermanentExtension<T>> extends AbstractExtension<T> {
    public PermanentExtension( MongoDocument document ) {
        super( document );
    }
}
