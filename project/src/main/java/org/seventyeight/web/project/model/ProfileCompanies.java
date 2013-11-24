package org.seventyeight.web.project.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author cwolfgang
 */
public class ProfileCompanies extends Action<ProfileCompanies> implements Getable<ProfileCompanies> {

    private static Logger logger = LogManager.getLogger( ProfileCompanies.class );

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

    public boolean isAffiliatedWith( String companyId ) {
        logger.debug( "Is " + getParent() + " affiliated with " + companyId );

        logger.debug( "Document: " + document );
        MongoDocument d;
        try {
            d = ((MongoDocument)document.get( Company.COMPANIES )).getSubDocument( companyId, null );
            logger.debug( "SUB DOCUMENT: " + d );
        } catch( Exception e ) {
            return false;
        }

        return ( d != null && !d.isNull() );
    }

    public void addCompany( Company company, String positionTitle, int fromYear, int fromMonth, int toYear, int toMonth, boolean currentPosition ) {
        logger.debug( "Adding company " + company );

        MongoDocument cdoc = new MongoDocument().set( Company.COMPANY, company.getIdentifier() ).set( "added", new Date() );
        cdoc.set( "position", positionTitle );
        cdoc.set( "fromYear", fromYear ).set( "fromMonth", fromMonth );
        cdoc.set( "toYear", toYear ).set( "toMonth", toMonth );
        cdoc.set( "currentPosition", currentPosition );
        document.addToList( Company.COMPANIES, cdoc );

        ((Profile)parent).save();
    }

    @PostMethod
    public void doAdd( Request request, Response response ) throws NoAuthorizationException, IOException, ItemInstantiationException {
        response.setRenderType( Response.RenderType.NONE );
        request.checkPermissions( parent, ACL.Permission.ADMIN );

        String title = request.getValue( "companyTitle", null );
        String fromMonthString = request.getValue( "fromMonth", null );
        String fromYearString = request.getValue( "fromYear", null );
        String toMonthString = request.getValue( "toMonth", null );
        String toYearString = request.getValue( "toYear", null );
        logger.debug( "Adding " + title + " to " + this + ", from: " + fromMonthString + ", to: " + toMonthString );

        if( title != null ) {
            Company c;
            try {
                c = Company.getCompanyByTitle( title, this );

                /*
                if( isAffiliatedWith( c.getIdentifier() ) ) {
                    response.sendError( Response.SC_NOT_ACCEPTABLE, this + " already have " + c );
                    return;
                }
                */
            } catch( NotFoundException e ) {
                logger.debug( e );

                c = Company.create( title );
                c.save();
            }

            // Get dates
            int fromYear = 0;
            int fromMonth = 0;
            int toYear = 0;
            int toMonth = 0;
            try {
                fromYear = Integer.parseInt( fromYearString );
                fromMonth = Integer.parseInt( fromMonthString );
            } catch( NumberFormatException e ) {
                fromYear = 0;
                fromMonth = 0;
            }

            boolean currentPosition = request.getValue( "currentPosition", "" ).length() > 0;

            // Only do this if from* is given
            if( !currentPosition && fromMonth > 0 && fromYear > 0 ) {
                try {
                    toYear = Integer.parseInt( toYearString );
                    toMonth = Integer.parseInt( toMonthString );
                } catch( NumberFormatException e ) {
                    toYear = 0;
                    toMonth = 0;
                }
            }

            String positionTitle = request.getValue( "positionTitle", "" );

            addCompany( c, positionTitle, fromYear, fromMonth, toYear, toMonth, currentPosition );
            response.setStatus( HttpServletResponse.SC_OK );
        } else {
            logger.debug( "No company title given" );
            response.setStatus( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    public void doList( Request request, Response response ) throws IOException, TemplateException {
        PrintWriter writer = response.getWriter();
        writer.write( ( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "list.vm" ) ) );
    }

    public void doGetList( Request request, Response response ) throws IOException, TemplateException, NotFoundException, ItemInstantiationException {
        logger.debug( "Getting company list for{}", getParent() );
        response.setRenderType( Response.RenderType.NONE );

        List<MongoDocument> docs = document.getList( Company.COMPANIES );

        if( docs.size() > 0 ) {
            for( MongoDocument d : docs ) {
                Company n = Core.getInstance().getNodeById( this, d.get( "company", "" ) );
                d.set( "companyTitle", n.getTitle() );
                //d.set( "badge", Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( n, "badge.vm" ) );
            }
            PrintWriter writer = response.getWriter();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            writer.write( gson.toJson( docs ) );
        } else {
            response.getWriter().write( "{}" );
        }
    }

    public Profile getProfile() {
        return (Profile) parent;
    }

    public List<ProfileCompany> getProfileCompanies() throws NotFoundException, ItemInstantiationException {
        logger.debug( "Getting companies for{}", getParent() );

        List<MongoDocument> docs = document.getList( Company.COMPANIES );

        if( docs.size() > 0 ) {
            List<ProfileCompany> companies = new ArrayList<ProfileCompany>( docs.size() );

            for( MongoDocument d : docs ) {
                Company company = Core.getInstance().getNodeById( this, d.get( "company", "" ) );
                companies.add( new ProfileCompany( company, d ) );
            }

            // Sort the list
            Collections.sort( companies, sorter );

            return companies;
        } else {
            return Collections.emptyList();
        }
    }

    private static final PCSorter sorter = new PCSorter();

    private static class PCSorter implements Comparator<ProfileCompany> {

        @Override
        public int compare( ProfileCompany profileCompany, ProfileCompany profileCompany2 ) {
            if( profileCompany.pc.get( "fromYear", 0 ) == profileCompany2.pc.get( "fromYear", 0 ) ) {
                return profileCompany2.pc.get( "fromMonth", 0 ) - profileCompany.pc.get( "fromMonth", 0 );
            } else {
                return profileCompany2.pc.get( "fromYear", 0 ) - profileCompany.pc.get( "fromYear", 0 );
            }
        }
    }

    public static class ProfileCompany {
        private Company company;
        private MongoDocument pc;

        public ProfileCompany( Company company, MongoDocument pc ) {
            this.company = company;
            this.pc = pc;
        }

        public Company getCompany() {
            return company;
        }

        public MongoDocument getPc() {
            return pc;
        }
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
