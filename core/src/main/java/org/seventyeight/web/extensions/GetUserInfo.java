package org.seventyeight.web.extensions;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class GetUserInfo extends Action {

    public GetUserInfo( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getUrlName() {
        return "info";
    }

    @Override
    public String getDisplayName() {
        return "Info";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
