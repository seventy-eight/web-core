package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Resource;
import org.seventyeight.web.model.ResourceDescriptor;

/**
 * @author cwolfgang
 */
public class Company extends Resource<Company> {

    public Company( Node parent, MongoDocument document ) {
        super( parent, document );
    }



    public static class CompanyDescriptor extends ResourceDescriptor<Company> {

        @Override
        public String getType() {
            return "company";
        }

        @Override
        public String getDisplayName() {
            return "Company";
        }
    }
}
