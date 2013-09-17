package org.seventyeight.web.extensions.searchers;

import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Searchable;

/**
 * @author cwolfgang
 */
public class TitleSearch extends Searchable {
    @Override
    public Class<? extends Node> getClazz() {
        return Node.class;
    }

    @Override
    public String getName() {
        return "Title search";
    }

    @Override
    public String getMethodName() {
        return "title";
    }

    @Override
    public MongoDBQuery search( String term ) {
        return new MongoDBQuery().regex( "title", "(?i)" + term );
    }
}
