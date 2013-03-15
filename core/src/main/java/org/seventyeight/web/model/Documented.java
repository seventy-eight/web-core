package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 *         Date: 06-03-13
 *         Time: 08:54
 */
public interface Documented {

    public MongoDocument getDocument();
}
