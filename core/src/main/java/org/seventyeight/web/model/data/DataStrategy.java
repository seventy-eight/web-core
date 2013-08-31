package org.seventyeight.web.model.data;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cwolfgang
 */
public abstract class DataStrategy<N extends Node, T extends DataNode<N>> {

    protected ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<String, Object>(  );

    public void get( String nodeId, int offset, int number ) throws ItemInstantiationException {
        MongoDBQuery query = new MongoDBQuery().is( "nodeid", nodeId ).greaterThanEquals( "offset", offset );
        MongoDocument document = MongoDBCollection.get( getCollectionName() ).findOne( query );

        T dataNode = getDataNode( document );
    }

    protected synchronized Object getLock( String lockName ) {
        if( !locks.containsKey( lockName ) ) {
            locks.put( lockName, new Date() );
        }

        return locks.containsKey( lockName );
    }

    public long getCountPerNode() {
        return 100;
    }

    public abstract String getCollectionName();

    public abstract T getDataNode( MongoDocument document );

    public abstract void addDataNode( String identifier, N node );

}
