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
public class Venue extends Resource<Venue> {

    public Venue( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {
       /* Implementation is a no op */
    }

    public static class VenueDescriptor extends NodeDescriptor<Venue> {

        @Override
        public String getType() {
            return "venue";
        }

        @Override
        public String getDisplayName() {
            return "Venue";
        }
    }
}
