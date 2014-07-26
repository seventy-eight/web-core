package org.seventyeight.web.utilities;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class DocumentFinder {

    private int number;
    private int offset;

    private Node parent;

    private Request request;

    private String view = "view.vm";

    private MongoDBQuery query = new MongoDBQuery();
    private MongoDocument sort = new MongoDocument();

    private boolean removeExtensionFields = true;

    private Core core;

    public DocumentFinder( Core core, Node parent, Request request, int number, int offset ) {
        this.number = number;
        this.offset = offset;
        this.parent = parent;
        this.request = request;
        this.core = core;
    }

    public MongoDBQuery getQuery() {
        return query;
    }

    public MongoDocument getSort() {
        return sort;
    }

    public List<MongoDocument> findNext() throws NotFoundException, ItemInstantiationException, TemplateException {
        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, offset, number, sort );

        for(MongoDocument d : docs) {
            Node n = core.getNodeById( parent, d.getIdentifier() );
            d.set( "view", core.getTemplateManager().getRenderer( request ).renderObject( n, view ) );

            if( removeExtensionFields ) {
                d.removeField( "extensions" );
            }
        }

        // Update
        offset += number;

        return docs;
    }
}
