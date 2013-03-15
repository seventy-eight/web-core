package org.seventyeight.web.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;

/**
 * @author cwolfgang
 *         Date: 27-02-13
 *         Time: 09:06
 */
public interface Savable {
    public MongoDocument getDocument();
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException;
}
