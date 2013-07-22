package org.seventyeight.web.model;

import org.seventyeight.database.mongodb.MongoDBQuery;

/**
 * @author cwolfgang
 */
public abstract class Searchable {

    public enum Operator {
        EQUALS,
        SIMILAR,
        LESS_THAN,
        LESS_THAN_OR_EQUALS,
        MORE_THAN,
        MORE_THAN_OR_EQUALS,
        NOT_EQUALS
    }

    public abstract Class<? extends Node> getClazz();
    public abstract String getName();
    public abstract String getMethodName();

    public abstract void search( MongoDBQuery query, Operator operator, String term ) throws SearchException;
}
