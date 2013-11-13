package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Company extends Resource<Company> {

    public static final String COMPANY = "company";
    public static final String COMPANIES = "companies";
    public static final String COMPANY_NAME = "Company";

    public static final String COMPANY_DOTTED = COMPANIES + "." + COMPANY;


    public Company( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    public static Company getCompanyByTitle( String title, Node parent ) throws NotFoundException {
        MongoDocument doc = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ).is( "type", COMPANY ) );
        if( !doc.isNull() ) {
            return new Company( parent, doc );
        } else {
            throw new NotFoundException( "The company \"" + title + "\" was not found" );
        }
    }

    public static Company create( String companyName ) throws ItemInstantiationException {
        Company company = ((CompanyDescriptor)Core.getInstance().getDescriptor( Company.class )).newInstance( companyName );
        return company;
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
