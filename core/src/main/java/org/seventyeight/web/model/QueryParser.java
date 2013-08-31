package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 *
 * methodName = something
 */
public class QueryParser {

    private static Logger logger = Logger.getLogger( QueryParser.class );

    public static final String regex = "([\\w-]+)\\s*:\\s*(?:\"([^\"]*)\"|([\\S]+))|(?:\"([^\"]*)\"|([\\S]+))";
    private static Pattern pattern = Pattern.compile( regex );


    public MongoDBQuery parse( String query ) {
        Matcher m = pattern.matcher( query );

        MongoDBQuery dbquery = new MongoDBQuery();

        while( m.find() ) {
            /* It's a method! */
            if( m.group( 1 ) != null ) {
                Searchable s = Core.getInstance().getSearchables().get( m.group( 1 ) );

                if( s != null ) {
                    logger.debug( s.getName() );
                    String term = "";
                    /* With quotes */
                    if( m.group( 2 ) != null ) {
                        term = m.group( 2 );
                    /* Without quotes */
                    } else if( m.group( 3 ) != null ) {
                        term = m.group( 3 );
                    }

                    s.search( dbquery, term );
                } else {
                    logger.debug( "Unknown method " + m.group( 1 ) );
                }

            /* Just a term with quotes */
            } else if( m.group( 3 ) != null ) {

            /* Just a term without quotes */
            } else if( m.group( 3 ) != null ) {

            }
        }

        return dbquery;
    }
}
