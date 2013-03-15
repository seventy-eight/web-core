package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 17:25
 */
public abstract class AbstractExtension extends PersistedObject implements Describable {

    public AbstractExtension( MongoDocument document ) {
        super( document );
    }

    @Override
    public Descriptor<?> getDescriptor() {
        return Core.getInstance().getDescriptor( getClass() );
    }
}
