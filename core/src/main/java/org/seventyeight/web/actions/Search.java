package org.seventyeight.web.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.utilities.ExecuteUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Search implements Node {

    private static Logger logger = Logger.getLogger( Search.class );

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

    //@PostMethod
    public void doSearch( Request request, Response response ) throws IOException {
        int offset = request.getInt( "offset", 0 );
        int number = request.getInt( "number", 10 );
        String query = request.getValue( "query", null );

        logger.debug( query + ", OFFSET: " + offset + ", NUMBER: " + number );

        if( query != null && !query.isEmpty() ) {
            QueryParser parser = new QueryParser();

            MongoDBQuery dbquery = parser.parse( query );
            logger.debug( "QUERY: " + dbquery );

            List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( dbquery, offset, number );

            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( docs ) );
        } else {
            response.getWriter().write( "{}" );
        }
    }

    @PostMethod
    public void doIndex2( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        String query = request.getValue( "query", null );
        logger.debug( "Searching for " + query );

        PrintWriter writer = response.getWriter();

        if( query != null ) {

            QueryParser parser = new QueryParser();

            MongoDBQuery dbquery = parser.parse( query );
            logger.debug( "QUERY: " + dbquery );

            List<MongoDocument> docs = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).find( dbquery, 0, 10 );

            NodeList nodes = new NodeList();

            for( MongoDocument d : docs ) {
                //response.getWriter().write( d.toString() );
                nodes.getNodes().add( Core.getInstance().getNodeById( this, (String) d.get( "_id" ) ) );
            }

            ExecuteUtils.render( request, response, nodes, "index" );

        } else {
            response.sendRedirect( "" );
        }
    }
}
