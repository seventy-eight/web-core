package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 */
public interface Savable {
    public MongoDocument getDocument();
    public void updateNode( JsonObject jsonData );
    public void save();
}
