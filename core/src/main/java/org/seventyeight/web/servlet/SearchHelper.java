package org.seventyeight.web.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.FeatureSearch;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class SearchHelper {

    private static Logger logger = LogManager.getLogger( SearchHelper.class  );

    private Request request;

    private Response response;

    private Node parent;

    private Response.RenderType renderType = Response.RenderType.NONE;

    private String contentType = "json/app";

    private int defaultOffset = 0;

    private int defaultNumber = 10;

    private boolean removeExtensionFields = true;

    private boolean renderBadge = true;

    private List<MongoDocument> documents;

    public SearchHelper( Node parent, Request request, Response response ) {
        this.request = request;
        this.response = response;
        this.parent = parent;
    }


    public SearchHelper search() {
        int offset = request.getInteger( "offset", defaultOffset );
        int number = request.getInteger( "number", defaultNumber );
        String query = request.getValue( "query", null );

        logger.debug( query + ", OFFSET: " + offset + ", NUMBER: " + number );

        if( query != null && !query.isEmpty() ) {
            MongoDBQuery dbquery = FeatureSearch.getSimpleQuery( query );
            logger.debug( "QUERY: " + dbquery );

            documents = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( dbquery, offset, number );

            logger.debug( "DOCS: " + documents );
        } else {
            documents = new ArrayList<MongoDocument>( 0 );
        }

        logger.debug( "SIZE: " + documents.size() );

        return this;
    }

    public void render() throws TemplateException, NotFoundException, ItemInstantiationException, IOException {
        response.setRenderType( renderType );
        response.setContentType( contentType );

        if( documents.size() > 0 ) {
            for( MongoDocument d : documents ) {
                logger.debug( "TYPE: {}, {}", d.get( "type", "N/A" ), d.getIdentifier() );
                Node n = Core.getInstance().getNodeById( parent, d.getIdentifier() );

                if( renderBadge ) {
                    d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );
                }

                if( removeExtensionFields ) {
                    d.removeField( "extensions" );
                }
            }

            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( documents ) );
        } else {
          response.getWriter().write( "{}" );
        }
    }

    public List<MongoDocument> getDocuments() {
        return documents;
    }
}
