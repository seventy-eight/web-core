package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDBQuery;

/**
 * @author cwolfgang
 *
 */
public abstract class Searchable {

    public abstract Class<? extends Node> getClazz();
    public abstract String getName();
    public abstract String getMethodName();

    public abstract void search( MongoDBQuery query, String term );
}
