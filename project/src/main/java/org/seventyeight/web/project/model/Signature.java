package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.ProjectCore;
import org.seventyeight.web.actions.AbstractUploadAction;
import org.seventyeight.web.extensions.NodeExtension;
import org.seventyeight.web.model.*;

import java.io.File;

/**
 * @author cwolfgang
 */
public class Signature extends AbstractUploadAction<Signature> {

    public Signature( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public File getPath() {
        return ((ProjectCore) Core.getInstance()).getSignaturePath();
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

    public static class SignatureDescriptor extends Action.ActionDescriptor<Signature> {

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
