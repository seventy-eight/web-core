package org.seventyeight.web.model.data;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDocument;

import java.util.Date;

/**
 * @author cwolfgang
 */
public abstract class DataElement {

    public static final String NODEID = "nodeid";
    public static final String ADDED = "added";

    protected MongoDocument document;

    public DataElement( MongoDocument document ) {
        this.document = document;
    }

    public Date getAdded() {
        return document.get( ADDED );
    }

    public String getNodeIdentifier() {
        return document.get( NODEID );
    }

    protected static MongoDocument createDocument( String identifier ) {
        MongoDocument d = new MongoDocument(  ).set( ADDED, new Date() ).set( NODEID, identifier );
        return d;
    }

    public void save() {
        MongoDBCollection.get( getCollectionName() ).save( document );
    }

    public abstract String getCollectionName();
}
