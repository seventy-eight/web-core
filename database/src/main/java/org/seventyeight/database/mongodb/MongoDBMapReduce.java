package org.seventyeight.database.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author cwolfgang
 */
public class MongoDBMapReduce {

    private static Logger logger = Logger.getLogger( MongoDBMapReduce.class );

    private String mapFunction;
    private String reduceFunction;
    private DBCollection collection;
    private MapReduceCommand.OutputType outputType = MapReduceCommand.OutputType.INLINE;
    private String outputCollectionName;
    private String outputDatabase;
    private MongoDBQuery query;

    public MongoDBMapReduce( File mapFile, File reduceFile ) throws IOException {
        mapFunction = FileUtils.readFileToString( mapFile );
        reduceFunction = FileUtils.readFileToString( reduceFile );
    }

    public MongoDBMapReduce( String mapFunction, String reduceFunction ) {
        this.mapFunction = mapFunction;
        this.reduceFunction = reduceFunction;
    }

    public MongoDBMapReduce setCollection( String collectionName ) {
        this.collection = MongoDBCollection.get( collectionName ).getCollection();
        return this;
    }

    public MongoDBMapReduce setOutputDatabase( String outputDatabase ) {
        this.outputDatabase = outputDatabase;
        return this;
    }

    public MongoDBMapReduce setQuery( MongoDBQuery query ) {
        this.query = query;
        return this;
    }

    public MapReduceOutput execute() {
        return execute( outputCollectionName, null );
    }

    public MapReduceOutput execute( String outputCollectionName ) {
        return execute( outputCollectionName, null );
    }

    public MapReduceOutput execute( MongoDBQuery query ) {
        return execute( outputCollectionName, query );
    }

    public MapReduceOutput execute( String outputCollectionName, MongoDBQuery query ) {
        logger.debug( "Executing map reduce" );

        MapReduceCommand cmd;
        if( query != null ) {
            cmd = new MapReduceCommand( collection, mapFunction, reduceFunction,  outputCollectionName, outputType, query.getDocument() );
        } else {
            cmd = new MapReduceCommand( collection, mapFunction, reduceFunction,  outputCollectionName, outputType, null );
        }

        if( this.outputDatabase != null ) {
            cmd.setOutputDB( outputDatabase );
        }

        return collection.mapReduce(cmd);
    }

    public String getMapFunction() {
        return mapFunction;
    }

    public String getReduceFunction() {
        return reduceFunction;
    }

    public MongoDBMapReduce setOutputCollection( String outputCollectionName ) {
        this.outputType = MapReduceCommand.OutputType.REDUCE;
        this.outputCollectionName = outputCollectionName;

        return this;
    }

    public MongoDBMapReduce setOutputType( MapReduceCommand.OutputType outputType ) {
        this.outputType = outputType;
        return this;
    }
}
