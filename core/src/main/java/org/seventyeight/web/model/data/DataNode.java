package org.seventyeight.web.model.data;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.Date;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 *
 * {
 *     nodeid: id,
 *     offset: 0,
 *     number: 100,
 *     elements: [
 *          {node1},
 *          {node2},...,
 *          {noden}
 *     ]
 * }
 */
public abstract class DataNode<T extends Node> implements Node {

    protected MongoDocument document;

    public DataNode( MongoDocument document ) {
        this.document = document;
    }

    public int getOffset() {
        return document.get( "offset", 0 );
    }

    public int getNumber() {
        return document.get( "number", 1 );
    }

    public <E extends Element> List<E> getElements( int index, int number ) {
        List<MongoDocument> docs = document.getList( "elements", index, number );

        List<E> elements = new ArrayList<E>( docs.size() );

        for( MongoDocument d : docs ) {
            elements.add( this.<E>getElement( d ) );
        }

        return elements;
    }

    public List<T> getNodes( int index, int number ) throws NotFoundException, ItemInstantiationException {
        List<MongoDocument> docs = document.getList( "elements", index, number );

        List<T> nodes = new ArrayList<T>( docs.size() );

        for( MongoDocument d : docs ) {
            T node = Core.getInstance().getNodeById( this, (String) d.get( "identifier" ) );
            nodes.add( node );
        }

        return nodes;
    }

    public abstract <E extends Element> E getElement( MongoDocument document );

    public class Element<T> {

        protected MongoDocument document;

        public Element( MongoDocument document ) {
            this.document = document;
        }

        public String getIdentifier() {
            return document.get( "identifier" );
        }

        public Date getAdded() {
            return document.get( "added" );
        }

        public T getNode() throws NotFoundException, ItemInstantiationException {
            return (T) Core.getInstance().getNodeById( DataNode.this, getIdentifier() );
        }

    }
}
