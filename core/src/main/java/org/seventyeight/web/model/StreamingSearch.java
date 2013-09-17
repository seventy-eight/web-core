package org.seventyeight.web.model;

import com.mongodb.MapReduceCommand;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBMapReduce;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.Core;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cwolfgang
 *
 * TO BE IMPLMENTED
 */
public class StreamingSearch {

    private static Logger logger = Logger.getLogger( StreamingSearch.class );

    private static final File mapFile = new File( StreamingSearch.class.getResource( "search.resources.map.js" ).getFile() );
    private static final File reduceFile = new File( StreamingSearch.class.getResource( "search.resources.reduce.js" ).getFile() );

    //public static final String regex = "([\\w-]+)\\s*:\\s*(?:\"([^\"]*)\"|([\\S]+))|(?:\"([^\"]*)\"|([\\S]+))";
    public static final String regex = "(?:([\\w-]+)\\s*:\\s*(?:\"([^\"]*)\"|([\\S]+)))|(?:\"([^\"]*)\"|([\\S]+))";
    public static final String regexSimple = "(?:\"([^\"]*)\"|([\\S]+))";
    private static Pattern pattern = Pattern.compile( regex );
    private static Pattern simplePattern = Pattern.compile( regexSimple );

    /** The {@link org.seventyeight.database.mongodb.MongoDBQuery} containing all the queries to the resources collection */
    private MongoDBQuery dbqueryResources = new MongoDBQuery();

    /** The {@link org.seventyeight.database.mongodb.MongoDBQuery} containing all the queries to the data collection */
    private MongoDBQuery dbqueryData = new MongoDBQuery();

    /** The end name of the collection */
    private String collectionName;

    /**
     * Get a simple {@link org.seventyeight.database.mongodb.MongoDBQuery} for only resources, tags, title...?
     */
    public static MongoDBQuery getSimpleQuery( String query ) {
        logger.debug( "Getting simple query: " + query );
        Matcher m = simplePattern.matcher( query );

        MongoDBQuery titles = new MongoDBQuery();
        MongoDBQuery tags = new MongoDBQuery();

        while( m.find() ) {
            for( int i = 0 ; i < m.groupCount() ; i++ ) {
                logger.debug( "[" + i + "] " + m.group( i ) );
            }
             if( m.group( 1 ) != null ) {
                titles.addIn( "title", m.group( 1 ) );
                tags.addIn( "tags", m.group( 1 ) );
            } else if( m.group( 2 ) != null ) {
                titles.addIn( "title", m.group( 2 ) );
                tags.addIn( "tags", m.group( 2 ) );
            }
        }

        MongoDBQuery finalQuery = new MongoDBQuery();
        finalQuery.or( true, titles, tags );
        logger.debug( "FINAL: " + finalQuery );

        return finalQuery;
    }


    /**
     * Parse the query and build up the search data.
     */
    public StreamingSearch parseQuery( String query ) {
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

    public StreamingSearch generate() throws IOException {

        /* Do the resources */
        String mapFunction = replaceMap();
        String reduceFunction = replaceMap();

        /* Generate collection name */
        collectionName = "search" + System.currentTimeMillis();

        MongoDBMapReduce mmr = new MongoDBMapReduce( mapFunction, reduceFunction ).setCollection( Core.RESOURCES_COLLECTION_NAME ).setOutputType( MapReduceCommand.OutputType.REDUCE );
        mmr.execute( collectionName );

        return this;
    }

    public String getCollectionName() {
        return collectionName;
    }

    private String replaceMap() throws IOException {

        String function = FileUtils.readFileToString( mapFile );
        return function;
    }

    private String replaceReduce() throws IOException {

        String function = FileUtils.readFileToString( reduceFile );
        return function;
    }
}
