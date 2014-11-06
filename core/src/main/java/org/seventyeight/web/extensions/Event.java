package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
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
    private static final SimpleDateFormat sdfDate = new SimpleDateFormat( "yyyy-MM-dd" );

    public Event( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
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
    public void updateNode( JsonObject jsonData ) {
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

        fromDateString += !fromTimeString.isEmpty() ? " " + fromTimeString : " 00:00";
            toDateString += !toTimeString.isEmpty() ? " " + toTimeString : " 00:00";

            if(fromTimeString.isEmpty() || toTimeString.isEmpty()) {
                allDay = true;
            }

            logger.debug( "FROM DATE STRING: {}", fromDateString );

            Date fromDate = null;
            Date toDate = null;
            try {
                fromDate = sdf.parse( fromDateString );
                toDate = sdf.parse( toDateString );

                document.set( "fromDate", fromDate );
                document.set( "toDate", toDate );
            } catch( ParseException e ) {
                throw new IllegalArgumentException( e );
            }

            logger.debug( "FROM: {}", fromDate );
        }
    }

    public Date getFromDate() {
        return document.get("fromDate");
    }

    public String getFromDateString() {
        Date d = getFromDate();
        if(d != null) {
            return sdfDate.format( d );
        } else {
            return "";
        }
    }

    public Date getToDate() {
        return document.get("toDate");
    }

    public String getToDateString() {
        Date d = getToDate();
        if(d != null) {
            return sdfDate.format( d );
        } else {
            return "";
        }
    }

    public static final class EventDescriptor extends ExtensionDescriptor<Event> {

        public EventDescriptor( Core core ) {
            super();
        }

        @Override
        public String getDisplayName() {
            return "Event";
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