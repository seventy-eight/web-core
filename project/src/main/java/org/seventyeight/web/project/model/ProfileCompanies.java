package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.PostMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.authentication.NoAuthorizationException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author cwolfgang
 */
public class ProfileCompanies extends Action<ProfileCompanies> implements Getable<ProfileCompanies> {

    private static Logger logger = Logger.getLogger( ProfileCompanies.class );

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
        logger.debug( "Is " + this + " affiliated with " + companyId );

        logger.debug( "Document: " + document );
        MongoDocument d;
        try {
            d = ((MongoDocument)document.get( Skill.SKILLS )).getSubDocument( companyId, null );
        } catch( Exception e ) {
            return false;
        }

        return ( d != null && !d.isNull() );
    }

    public void addCompany( Company company, int fromYear, int fromMonth, int toYear, int toMonth ) {
        logger.debug( "Adding company " + company );

        MongoDocument cdoc = new MongoDocument().set( Company.COMPANY, company.getIdentifier() ).set( "added", new Date() );
        cdoc.set( "fromYear", fromYear ).set( "fromMonth", fromMonth );
        cdoc.set( "toYear", toYear ).set( "toMonth", toMonth );
        document.addToList( Company.COMPANIES, cdoc );

        ((Profile)parent).save();
    }

    @PostMethod
    public void doAdd( Request request, Response response ) throws NoAuthorizationException, IOException, ItemInstantiationException {
        request.setResponseType( Request.ResponseType.HTTP_CODE );
        request.checkAuthorization( (Authorizer) parent, Authorizer.Authorization.MODERATE );

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

                if( isAffiliatedWith( c.getIdentifier() ) ) {
                    response.sendError( Response.SC_NOT_ACCEPTABLE, this + " already have " + c );
                    return;
                }
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

            try {
                toYear = Integer.parseInt( toYearString );
                toMonth = Integer.parseInt( toMonthString );
            } catch( NumberFormatException e ) {
                toYear = 0;
                toMonth = 0;
            }

            addCompany( c, fromYear, fromMonth, toYear, toMonth );
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
