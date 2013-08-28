package org.seventyeight.web.model.data;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;

import java.lang.reflect.Constructor;

/**
 * @author cwolfgang
 */
public abstract class DataStrategy<N extends Node, T extends DataNode<N>> {

    public void get( String nodeId, int offset, int number ) throws ItemInstantiationException {
        MongoDBQuery query = new MongoDBQuery().is( "nodeid", nodeId ).greaterThanEquals( "offset", offset );
        MongoDocument document = MongoDBCollection.get( getCollectionName() ).findOne( query );

        T dataNode = getItem( document, DataNode.class );
    }

    public abstract String getCollectionName();


    public <T extends DataNode> T getItem( MongoDocument document, Class<?> clazz ) throws ItemInstantiationException {
        try {
            Constructor<?> c = clazz.getConstructor( MongoDocument.class );
            return (T) c.newInstance( document );
        } catch( Exception e ) {
            throw new ItemInstantiationException( "Unable to get the class " + clazz, e );
        }
    }
}
