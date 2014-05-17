package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;

/**
 * @author cwolfgang
 */
public class Event extends ResourceExtension<Event> {

    public Event( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return "Event";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public void updateNode( CoreRequest request, JsonObject jsonData ) {

    }

    public static final class EventDescriptor extends ExtensionDescriptor<Event> {

        @Override
        public String getDisplayName() {
            return "Event";
        }

        @Override
        public String getExtensionName() {
            return "event";
        }

        @Override
        public String getTypeName() {
            return "event";
        }

        @Override
        public ExtensionGroup getExtensionGroup() {
            return new ExtensionGroup( getClazz(), "Event" );
        }

        @Override
        public Class<Event> getExtensionClass() {
            return Event.class;
        }
    }
}