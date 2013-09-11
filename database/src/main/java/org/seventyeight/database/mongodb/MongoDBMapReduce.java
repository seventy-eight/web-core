package org.seventyeight.database.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author cwolfgang
 */
public class MongoDBMapReduce {

    private String mapFunction;
    private String reduceFunction;
    private DBCollection collection;
    private MapReduceCommand.OutputType outputType = MapReduceCommand.OutputType.INLINE;
    private String outputCollectionName;

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

    public MapReduceOutput execute() {
        MapReduceCommand cmd = new MapReduceCommand( collection, mapFunction, reduceFunction,  outputCollectionName, outputType, null);

        return collection.mapReduce(cmd);
    }

    public MapReduceOutput execute( String outputCollectionName ) {
        MapReduceCommand cmd = new MapReduceCommand( collection, mapFunction, reduceFunction,  outputCollectionName, outputType, null);

        return collection.mapReduce(cmd);
    }

    public MapReduceOutput execute( String outputCollectionName, MongoDBQuery query ) {
        MapReduceCommand cmd = new MapReduceCommand( collection, mapFunction, reduceFunction,  outputCollectionName, outputType, query.getDocument() );

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
