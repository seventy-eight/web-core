package org.seventyeight.web.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.search.SearchFormatter;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.FeatureSearch;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cwolfgang
 */
public class ResourcesAction implements Node {

    private static Logger logger = LogManager.getLogger( ResourcesAction.class );

    private ConcurrentHashMap<String, SearchFormatter> formatters = new ConcurrentHashMap<String, SearchFormatter>(  );

    @Override
    public Node getParent() {
        return Core.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Resources";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public void addFormatter( SearchFormatter formatter ) {
        logger.debug( "Adding formatter {}", formatter.getName() );
        formatters.put( formatter.getName(), formatter );
    }

    @PostMethod
    public void doSearch( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );
        response.setContentType( "json/app" );

        int offset = request.getInteger( "offset", 0 );
        int number = request.getInteger( "number", 10 );
        String query = request.getValue( "query", null );

        logger.debug( query + ", OFFSET: " + offset + ", NUMBER: " + number );

        if( query != null && !query.isEmpty() ) {
            MongoDBQuery dbquery = FeatureSearch.getSimpleQuery( query );
            logger.debug( "QUERY: " + dbquery );

            List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( dbquery, offset, number );

            logger.debug( "DOCS: " + docs );

            //
            String[] formatters = request.getValue( "format", "" ).split( "," );


            for( MongoDocument d : docs ) {
                logger.debug( "TYPE: {}, {}", d.get( "type", "N/A" ), d.getIdentifier() );
                Node n = Core.getInstance().getNodeById( this, d.getIdentifier() );
                d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );

                if( formatters.length > 0 ) {
                    for( String formatter : formatters ) {
                        logger.debug( "FORMAYTERERE: {}, {}", formatter, this.formatters.get( formatter ) );
                        this.formatters.get( formatter ).format( d, n );
                    }
                }
                //d.removeField( "extensions" );
            }

            logger.debug( "SIZE: " + docs.size() );

            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( docs ) );
        } else {
            response.getWriter().write( "{}" );
        }
    }


    public void doList( Request request, Response response ) throws IOException {
        response.setRenderType( Response.RenderType.NONE );
        response.setContentType( "json/app" );

        int offset = request.getInteger( "offset", 0 );
        int number = request.getInteger( "number", 10 );
        String type = request.getValue( "type", "" );
        String username = request.getValue( "username", "" );

        MongoDBQuery query = new MongoDBQuery();
        if( !type.isEmpty() ) {
            query.is( "type", type );
        }
        if( !username.isEmpty() ) {
            User user = User.getUserByUsername( this, username );
            query.is( "owner", user.getIdentifier() );
        }

        logger.debug( "Query: {}", query );

        List<MongoDocument> docs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, offset, number );

        if( docs.size() > 0 ) {
            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( docs ) );
        } else {
            response.getWriter().write( "{}" );
        }
    }
}
