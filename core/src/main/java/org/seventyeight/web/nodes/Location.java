package org.seventyeight.web.nodes;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Location extends Resource<Location> {

    public Location( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
        /* Implementation is a no op */
    }

    public static class LocationDescriptor extends NodeDescriptor<Location> {

        protected LocationDescriptor( Node parent ) {
            super( parent );
        }

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
