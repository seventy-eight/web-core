package org.seventyeight.web.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 *
 * methodName = something
 */
public class QueryParser {

    private static Logger logger = Logger.getLogger( QueryParser.class );

    //public static final String regex = "([\\w-]+)\\s*:\\s*(?:\"([^\"]*)\"|([\\S]+))|(?:\"([^\"]*)\"|([\\S]+))";
    public static final String regex = "(?:([\\w-]+)\\s*:\\s*(?:\"([^\"]*)\"|([\\S]+)))|(?:\"([^\"]*)\"|([\\S]+))";
    private static Pattern pattern = Pattern.compile( regex );


    public MongoDBQuery parse( String query ) {
        Matcher m = pattern.matcher( query );

        MongoDBQuery dbquery = new MongoDBQuery();

        MongoDBQuery titles = new MongoDBQuery();
        MongoDBQuery tags = new MongoDBQuery();

        while( m.find() ) {
            for( int i = 0 ; i < m.groupCount() ; i++ ) {
                logger.debug( "[" + i + "] " + m.group( i ) );
            }
            /* It's a method! */
            if( m.group( 1 ) != null ) {
                logger.debug( "ITS A METHOD" );
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

            /*QUE?!*/
            } else if( m.group( 3 ) != null ) {
                //dbquery.addIn( "title", m.group( 3 ) );
            /* Just a term with quotes */
            } else if( m.group( 4 ) != null ) {
                titles.addIn( "title", m.group( 4 ) );
                tags.addIn( "tags", m.group( 4 ) );
            /* Just a term without quotes */
            } else if( m.group( 5 ) != null ) {
                titles.addIn( "title", m.group( 5 ) );
                tags.addIn( "tags", m.group( 5 ) );
            }
        }

        logger.debug( "FINAL" );
        MongoDBQuery finalQuery = new MongoDBQuery();
        finalQuery.or( true, dbquery, titles, tags );

        return finalQuery;
    }
}
