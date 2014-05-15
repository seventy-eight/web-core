package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public abstract class AbstractEvent<T extends AbstractEvent> extends Resource<T> implements Dateable {
    public AbstractEvent( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public abstract class AbstractEventDescriptor<DT extends AbstractEvent> extends NodeDescriptor<DT> {

    }
}
