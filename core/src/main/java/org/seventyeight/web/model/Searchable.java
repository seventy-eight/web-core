package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDBQuery;

/**
 * @author cwolfgang
 *
 */
public abstract class Searchable {

    public enum CollectionType {
        DATA,
        RESOURCE
    }

    public abstract Class<? extends Node> getClazz();
    public abstract String getName();
    public abstract String getMethodName();

    public CollectionType getType() {
        return CollectionType.RESOURCE;
    }

    public abstract MongoDBQuery search( String term );
}
