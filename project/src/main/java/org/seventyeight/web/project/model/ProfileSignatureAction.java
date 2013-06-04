package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.ProjectCore;
import org.seventyeight.web.actions.AbstractUploadAction;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;

import java.io.File;

/**
 * @author cwolfgang
 */
public class ProfileSignatureAction extends AbstractUploadAction {

    protected ProfileSignatureAction( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public File getPath() {
        return ((ProjectCore)Core.getInstance()).getSignaturePath();
    }

    @Override
    public String getUrlName() {
        return "signature";
    }

    @Override
    public Node getChild( String name ) throws NotFoundException {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Signature";
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }
}
