package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Signature extends Action<Signature> {

    public Signature( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUrlName() {
        return "signature";
    }

    @Override
    public String getDisplayName() {
        return "Signature";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public static class SignatureDescriptor extends ActionDescriptor<Signature> {

        @Override
        public String getDisplayName() {
            return "Signature";
        }

        @Override
        public String getExtensionName() {
            return "signature";
        }

    }

}
