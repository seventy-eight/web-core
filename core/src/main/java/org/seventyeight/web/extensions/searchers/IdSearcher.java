package org.seventyeight.web.extensions.searchers;

import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Searchable;

/**
 * @author cwolfgang
 */
public class IdSearcher extends Searchable {

    @Override
    public Class<? extends Node> getClazz() {
        return Node.class;
    }

    @Override
    public String getName() {
        return "Search id";
    }

    @Override
    public String getMethodName() {
        return "id";
    }

    @Override
    public void search( MongoDBQuery query, String term ) {
        query.getId( term );
    }
}
