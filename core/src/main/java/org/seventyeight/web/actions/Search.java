package org.seventyeight.web.actions;

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
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Search implements Node {

    private static Logger logger = LogManager.getLogger( Search.class );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Search";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public void doSearch( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );
        int offset = request.getInt( "offset", 0 );
        int number = request.getInt( "number", 10 );
        String query = request.getValue( "query", null );

        logger.debug( query + ", OFFSET: " + offset + ", NUMBER: " + number );

        if( query != null && !query.isEmpty() ) {
            MongoDBQuery dbquery = FeatureSearch.getSimpleQuery( query );
            logger.debug( "QUERY: " + dbquery );

            List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( dbquery, offset, number );

            for( MongoDocument d : docs ) {
                Node n = Core.getInstance().getNodeById( this, d.getIdentifier() );
                d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );
                d.removeField( "extensions" );
            }

            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( docs ) );
        } else {
            response.getWriter().write( "{}" );
        }
    }

    public void doShow( Request request, Response response ) {
        logger.debug( "SHOW????!!!!" );
    }



}
