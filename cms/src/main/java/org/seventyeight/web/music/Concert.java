package org.seventyeight.web.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

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
