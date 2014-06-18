package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public interface Documented {
    public MongoDocument getDocument();
}
