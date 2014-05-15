package org.seventyeight.web.utilities;

import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Searchable;

/**
 * @author cwolfgang
 */
class TestSearchable extends Searchable {

    @Override
    public Class<? extends Node> getClazz() {
        return null;  /* Implementation is a no op */
    }

    @Override
    public String getName() {
        return "Test searchable";
    }

    @Override
    public String getMethodName() {
        return "test";
    }

    @Override
    public MongoDBQuery search( String term ) {
        return new MongoDBQuery().is( "term", term ).exists( "hej" );
    }
}

