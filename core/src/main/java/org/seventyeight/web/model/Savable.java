package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public interface Savable {
    public MongoDocument getDocument();
    public void updateNode(CoreRequest request);
    public void save();
}
