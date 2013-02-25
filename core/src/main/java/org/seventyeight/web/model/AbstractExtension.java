package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 *         Date: 18-02-13
 *         Time: 17:25
 */
public abstract class AbstractExtension extends AbstractModelObject {

    public AbstractExtension( MongoDocument document ) {
        super( document );
    }
}
