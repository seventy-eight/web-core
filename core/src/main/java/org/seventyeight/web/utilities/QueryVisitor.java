package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.ast.Comparison;
import org.seventyeight.ast.Value;
import org.seventyeight.ast.Visitor;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.model.Searchable;
import org.seventyeight.web.model.CoreSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cwolfgang
 */
public class QueryVisitor extends Visitor {
    private static Logger logger = LogManager.getLogger( QueryVisitor.class );

    private MongoDBQuery query = new MongoDBQuery();
    private MongoDBQuery features = new MongoDBQuery();

    private CoreSystem system;

    Map<String, MongoDBQuery> searchKeys = new HashMap<String, MongoDBQuery>(  );

    public QueryVisitor( CoreSystem system ) {
        this.system = system;

        for(String sk : system.getSearchKeyMap().keySet()) {
            searchKeys.put( sk, new MongoDBQuery() );
        }
    }

    public MongoDBQuery getQuery() {
        //return query;

        MongoDBQuery tq = new MongoDBQuery().or( true, searchKeys.values() );

        return new MongoDBQuery().and( true, features, tq );
    }

    @Override
    public void visit( Comparison comparison ) {
        String key = comparison.getLeftSide().toString();
        String term = comparison.getRightSide().toString();
        Searchable s = system.getSearchables().get(key);

        if( s != null ) {
            logger.debug( s.getName() );
            features.addAnd( s.search( term ) );
        } else {
            logger.debug( "Unknown method {}", key );
        }
    }

    public void visit(Value value) {
        for(String sk : system.getSearchKeyMap().keySet() ) {
            searchKeys.get( sk ).addIn( system.getSearchKeyMap().get( sk ), value.getValue() );
        }

    }
}
