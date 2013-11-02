package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class ProfileCompanies extends Action<ProfileCompanies> implements Getable<ProfileCompanies> {

    public ProfileCompanies( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return "Profile companies";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @Override
    public ProfileCompanies get( String token ) throws NotFoundException {
        return null;  /* Implementation is a no op */
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
      /* Implementation is a no op */
    }

    public static class ProfileCompaniesDescriptor extends ActionDescriptor<ProfileCompanies> {

        @Override
        public String getDisplayName() {
            return "Profile companies";
        }

        @Override
        public String getExtensionName() {
            return "companies";
        }

        @Override
        public boolean isApplicable( Node node ) {
            return node instanceof Profile;
        }
    }
}
