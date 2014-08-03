package org.seventyeight.web.actions;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class GetAction extends Action<GetAction> implements Parent, DeletingParent {

    private static Logger logger = LogManager.getLogger( GetAction.class );

    public GetAction( Core core, Node parent, MongoDocument document ) {
        super( core, parent, document );
    }

    @Override
    public String getDisplayName() {
        return "Get";
    }

    @Override
    public String getMainTemplate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return (Node) ((Getable)parent).get( core, name );
    }

    @Override
    public void updateNode( JsonObject jsonData ) {
      /* Implementation is a no op */
    }

    @Override
    public void deleteChild( Node node ) {
        if(parent != null && parent instanceof DeletingParent) {
            ( (DeletingParent) parent ).deleteChild( node );
        } else {
            logger.debug( "No deleting operation for {}", this );
        }
    }

    public static class GetDescriptor extends Action.ActionDescriptor<GetAction> {

        public GetDescriptor( Core core ) {
            super( core );
        }

        @Override
        public String getDisplayName() {
            return "Get";
        }

        @Override
        public String getExtensionName() {
            return "get";
        }

        @Override
        public Class<GetAction> getExtensionClass() {
            return GetAction.class;
        }

        @Override
        public boolean isApplicable( Node node ) {
            return ( node instanceof Getable );
        }

        @Override
        public boolean isOmnipresent() {
            return true;
        }
    }
}
