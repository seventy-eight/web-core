package org.seventyeight.web.nodes;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Location extends Resource<Location> {

    public Location( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
        /* Implementation is a no op */
    }

    public static class LocationDescriptor extends NodeDescriptor<Location> {

        @Override
        public String getType() {
            return "location";
        }

        @Override
        public String getDisplayName() {
            return "Location";
        }
    }
}
