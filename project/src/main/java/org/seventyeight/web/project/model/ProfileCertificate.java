package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;

/**
 * @author cwolfgang
 */
public class ProfileCertificate implements Node {

    private Node parent;
    private Profile profile;
    private Certificate certificate;

    public ProfileCertificate( Node parent, Profile profile, Certificate certificate ) {
        this.certificate = certificate;
        this.profile = profile;
        this.parent = parent;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Profile certificate";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

}
