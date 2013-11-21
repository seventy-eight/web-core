package org.seventyeight.web.project.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.structure.Tuple;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Company extends Resource<Company> {

    private static Logger logger = LogManager.getLogger( Company.class );

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

        public void doSearch( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException {
            int offset = request.getInteger( "offset", 0 );
            int number = request.getInteger( "number", 10 );
            String term = request.getValue( "term", null );

            logger.debug( term + ", OFFSET: " + offset + ", NUMBER: " + number );

            response.setRenderType( Response.RenderType.NONE );

            if( term == null || term.isEmpty() ) {
                response.getWriter().print( "{}" );
            } else {
                MongoDBQuery dbquery = new MongoDBQuery().regex( "title", "(?i)" + term + ".*" ).is( "type", "company" );
                logger.debug( "QUERY: " + dbquery );

                List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( dbquery, offset, number );

                List<Tuple<String, String>> companies = new ArrayList<Tuple<String, String>>(  );

                for( MongoDocument d : docs ) {
                    logger.debug( "DOX: " + d );
                    companies.add( new Tuple<String, String>( d.get( "title", "" ), d.get( "_id", "" ) ) );
                }

                PrintWriter writer = response.getWriter();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                writer.write( gson.toJson( companies ) );
            }
        }
    }
}
