package org.seventyeight.web.actions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class GetAction extends Action<GetAction> implements Parent {

    public GetAction( Node parent, MongoDocument document ) {
        super( parent, document );
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
        return (Node) ((Getable)parent).get( name );
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class GetDescriptor extends Action.ActionDescriptor<GetAction> {

        @Override
        public String getDisplayName() {
            return "Get";
        }

        @Override
        public String getExtensionName() {
            return "get";
        }

        @Override
        public boolean isApplicable( Node node ) {
            return ( node instanceof Getable );
        }
    }
}
