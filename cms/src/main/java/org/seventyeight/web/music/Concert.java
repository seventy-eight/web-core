package org.seventyeight.web.music;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NodeDescriptor;
import org.seventyeight.web.model.Resource;

/**
 * @author cwolfgang
 */
public class Concert extends Resource<Concert> {

    public Concert( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
        if(jsonData != null) {
            String venueId = jsonData.get( "venueId" ).getAsString();
            document.set( "venue", venueId );
        }
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
