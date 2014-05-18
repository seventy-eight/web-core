package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.AbstractExtension;
import org.seventyeight.web.model.CoreRequest;
import org.seventyeight.web.model.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author cwolfgang
 */
public class Event extends ResourceExtension<Event> {

    private static Logger logger = LogManager.getLogger( Event.class );

    private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

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
        logger.debug( "Updating event, {}", jsonData );

        if(jsonData != null) {
            String fromDateString = jsonData.get( "eventFromDate" ).getAsString();
            String toDateString = jsonData.get( "eventToDate" ).getAsString();
            String fromTimeString = jsonData.get( "eventFromTime" ).getAsString();
            String toTimeString = jsonData.get( "eventToTime" ).getAsString();
            logger.debug( "-----> {}", jsonData.get( "eventAllDay" ) );
            boolean allDay = jsonData.get( "eventAllDay" ) != null ? true : false; // jsonData.get( "eventAllDay" ).getAsString().equalsIgnoreCase( "on" )

            if(fromDateString.isEmpty() || toDateString.isEmpty()) {
                throw new IllegalArgumentException( "No from or to date provided" );
            }

            logger.debug( "FROM TIME SREINR: '{}'", fromTimeString );

            fromDateString += !fromTimeString.isEmpty() ? fromTimeString : " 00:00";
            toDateString += !toTimeString.isEmpty() ? toTimeString : " 00:00";

            if(fromTimeString.isEmpty() || toTimeString.isEmpty()) {
                allDay = true;
            }

            logger.debug( "FROM DATE STRING: {}", fromDateString );

            Date fromDate = null;
            Date toDate = null;
            try {
                fromDate = sdf.parse( fromDateString );
                toDate = sdf.parse( toDateString );
            } catch( ParseException e ) {
                throw new IllegalArgumentException( e );
            }

            logger.debug( "FROM: {}", fromDate );
        }
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