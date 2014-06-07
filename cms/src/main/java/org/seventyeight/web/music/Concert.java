package org.seventyeight.web.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Concert extends Resource<Concert> implements Event {

    private static Logger logger = LogManager.getLogger( Concert.class );

    public Concert( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        if(jsonData != null) {
            JsonElement venueElement = jsonData.get( "venue" );
            logger.debug( "VENUE ELEMENT: {}", venueElement );
            if(venueElement.isJsonNull()) {
                throw new IllegalArgumentException( "Venue must be provided" );
            }
            document.set( "venue", venueElement.getAsString() );

            JsonElement artistsElement = jsonData.get( "artists" );
            if(artistsElement.isJsonNull()) {

            } else {
                JsonArray artistsArray = artistsElement.getAsJsonArray();
                List<String> artists = new ArrayList<String>( artistsArray.size() );
                for( JsonElement k : artistsArray) {
                    artists.add( k.getAsString() );
                }

                document.set( "artists", artists );
            }
        }
    }

    public void setVenue(Venue venue) {
        document.set( "venue", venue.getIdentifier() );
    }

    public String getVenueId() {
        return document.get( "venue", null );
    }

    public Venue getVenue() throws NotFoundException, ItemInstantiationException {
        return Core.getInstance().getNodeById( this, getVenueId() );
    }

    public void setAsPartOf(Resource<?> resource) {
        document.set( "partOf", resource.getIdentifier() );
    }

    public static class ConcertDescriptor extends NodeDescriptor<Concert> {

        public void doGetConcerts(Request request, Response response) throws IOException {
            response.setRenderType( Response.RenderType.NONE );

            String term = request.getValue( "term", "" );
            String venueTerm = request.getValue( "venue", null );

            if( term.length() > 1 ) {
                // First fetch artists
                MongoDBQuery artistQuery = new MongoDBQuery().is( "type", "artist" ).regex( "title", "(?i)" + term + ".*" );
                List<MongoDocument> artistDocs = MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( artistQuery, 0, 10 );

                MongoDBQuery query = new MongoDBQuery();
                List<MongoDBQuery> queries = new ArrayList<MongoDBQuery>( 3 );
                queries.add( new MongoDBQuery().is( "type", "concert" ).regex( "title", "(?i)" + term + ".*" ) );
                List<String> ids = new ArrayList<String>( artistDocs.size() );
                for(MongoDocument d : artistDocs) {
                    ids.add( d.get( "_id", "" ) );
                }
                queries.add( new MongoDBQuery().in( "artists", ids ) );

                query.or( true, queries );
                logger.debug( "QUERY IS {}", query );

                PrintWriter writer = response.getWriter();
                writer.print( MongoDBCollection.get( Core.NODES_COLLECTION_NAME ).find( query, 0, 10 ) );
            } else {
                response.getWriter().write( "{}" );
            }
        }

        @Override
        public String getType() {
            return "concert";
        }

        @Override
        public String getDisplayName() {
            return "Concert";
        }
    }
}
