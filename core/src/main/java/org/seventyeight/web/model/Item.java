package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 23:14
 */
public interface Item {
    public String getDisplayName();
    public MongoDocument getDocument();
}
