package org.seventyeight.web.extensions.footer;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Footer extends NodeExtension<Footer> {

    public Footer( MongoDocument document ) {
        super( document );
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) {
    }

    public String getFooter() {
        return "MY FOOT!ER!";
    }

    @Override
    public List<Action> getActions() {
        List<Action> actions = new ArrayList<Action>( 1 );
        actions.add( new FooterAction() );
        return actions;
    }

    public class FooterAction implements Action {

        @Override
        public Node getChild( String name ) throws NotFoundException {
            return null;
        }

        @Override
        public Node getParent() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "footer";
        }

        @Override
        public String getDisplayName() {
            return "Footer";
        }

        @Override
        public String getMainTemplate() {
            return "org/seventyeight/web/main.vm";
        }
    }

    public static class FooterDescriptor extends Descriptor<Footer> {

        @Override
        public String getDisplayName() {
            return "Footer";
        }
    }
}
