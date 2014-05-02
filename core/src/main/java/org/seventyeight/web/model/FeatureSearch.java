package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.Core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 *
 * methodName = something
 */
public class FeatureSearch {

    private static Logger logger = LogManager.getLogger( FeatureSearch.class );

    public static final String regex = "(?:([\\w-]+)\\s*:\\s*(?:\"([^\"]*)\"|([\\S]+)))|(?:\"([^\"]*)\"|([\\S]+))";
    public static final String regexSimple = "(?:\"([^\"]*)\"|([\\S]+))";
    private static Pattern pattern = Pattern.compile( regex );
    private static Pattern simplePattern = Pattern.compile( regexSimple );

    /** The {@link MongoDBQuery} containing all the queries to the resources collection */
    private MongoDBQuery dbqueryResources = new MongoDBQuery();

    /** The {@link MongoDBQuery} containing all the queries to the data collection */
    private MongoDBQuery dbqueryData = new MongoDBQuery();

    /** The end name of the collection */
    private String collectionName;

    /**
     * Get a simple {@link MongoDBQuery} for only resources, tags, title...?
     */
    public static MongoDBQuery getSimpleQuery( String query ) {
        logger.debug( "Getting simple query: " + query );
        Matcher m = pattern.matcher( query );

        MongoDBQuery titles = new MongoDBQuery();
        MongoDBQuery tags = new MongoDBQuery();

        Map<String, MongoDBQuery> searchKeys = new HashMap<String, MongoDBQuery>(  );
        for(String sk : Core.getInstance().getSearchKeyMap().keySet()) {
            searchKeys.put( sk, new MongoDBQuery() );
        }

        MongoDBQuery features = new MongoDBQuery();

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

                    features.addAnd( s.search( term ) );
                } else {
                    logger.debug( "Unknown method " + m.group( 1 ) );
                }

            /*QUE?!*/
            } else if( m.group( 3 ) != null ) {
                //dbqueryResources.addIn( "title", m.group( 3 ) );
            /* Just a term with quotes */
            } else if( m.group( 4 ) != null ) {
                //titles.addIn( "title", m.group( 4 ) );
                //tags.addIn( "tags", m.group( 4 ) );
                for(String sk : Core.getInstance().getSearchKeyMap().keySet()) {
                    searchKeys.get( sk ).addIn( Core.getInstance().getSearchKeyMap().get( sk ), m.group( 4 ) );
                }
            /* Just a term without quotes */
            } else if( m.group( 5 ) != null ) {
                //titles.addIn( "title", m.group( 5 ) );
                //tags.addIn( "tags", m.group( 5 ) );
                for(String sk : Core.getInstance().getSearchKeyMap().keySet()) {
                    searchKeys.get( sk ).addIn( Core.getInstance().getSearchKeyMap().get( sk ), m.group( 5 ) );
                }
            }
        }

        MongoDBQuery finalQuery = new MongoDBQuery();
        //finalQuery.and( true, features, titles, tags );
        //finalQuery.or( true, features, titles, tags );

        if( tags.length() > 0 || titles.length() > 0 ) {
            //MongoDBQuery tq = new MongoDBQuery().or( true, titles, tags );
            MongoDBQuery tq = new MongoDBQuery().or( true, searchKeys.values() );
            finalQuery.and( true, features, tq );
        } else {
            finalQuery = features;
        }

        //finalQuery.or( true, titles, tags );
        logger.debug( "FINAL: " + finalQuery );

        return finalQuery;
    }


    /**
     * Parse the query and build up the search data.
     */
    public FeatureSearch parseQuery( String query ) {
        Matcher m = pattern.matcher( query );

        MongoDBQuery titles = new MongoDBQuery();
        MongoDBQuery tags = new MongoDBQuery();

        MongoDBQuery trquery = new MongoDBQuery();

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

                    /*
                    if( s.getType().equals( Searchable.CollectionType.RESOURCE ) ) {
                        s.search( trquery, term );
                    } else {
                        s.search( dbqueryData, term );
                    }
                    */
                } else {
                    logger.debug( "Unknown method " + m.group( 1 ) );
                }

            /*QUE?!*/
            } else if( m.group( 3 ) != null ) {
                //dbqueryResources.addIn( "title", m.group( 3 ) );
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
        dbqueryResources.or( true, trquery, titles, tags );

        //return finalQuery;
        return this;
    }
}
