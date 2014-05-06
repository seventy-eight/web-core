package org.seventyeight.web.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.ast.Assignment;
import org.seventyeight.ast.Value;
import org.seventyeight.ast.Visitor;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Searchable;
import org.seventyeight.web.model.CoreSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class QueryVisitor extends Visitor {
    private static Logger logger = LogManager.getLogger( QueryVisitor.class );

    private MongoDBQuery query = new MongoDBQuery();
    private MongoDBQuery features = new MongoDBQuery();

    private CoreSystem system;

    private List<String> singleTerms = new ArrayList<String>(  );

    public QueryVisitor( CoreSystem system ) {
        this.system = system;
    }

    public MongoDBQuery getQuery() {
        //return query;

        for(String singleTerm : singleTerms) {

        }

        return new MongoDBQuery().and( true, features );
    }

    @Override
    public void visit( Assignment assignment ) {
        String key = assignment.getLeftSide().toString();
        String term = assignment.getRightSide().toString();
        Searchable s = system.getSearchables().get(key);

        if( s != null ) {
            logger.debug( s.getName() );
            features.addAnd( s.search( term ) );
        } else {
            logger.debug( "Unknown method {}", key );
        }
    }

    public void visit(Value value) {
        logger.debug( "YEAH, value {}", value );
    }
}
