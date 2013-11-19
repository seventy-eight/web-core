package org.seventyeight.web.project.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * {
 *     skill: {
 *         added: date  // The date added ??
 *         profile: pid // Last validator
 *     }
 * }
 * </pre>
 *
 * @author cwolfgang
 */
public class ProfileSkill implements Node {

    private static Logger logger = Logger.getLogger( ProfileSkill.class );

    public static final String LAST_VALIDATION = "lastValidation";
    public static final String VALIDATIONS_COLLECTION = "skillValidations";

    private Node parent;
    private Skill skill;
    private MongoDocument document;
    private List<Validation> validations;

    public ProfileSkill( Node parent, Skill skill, MongoDocument document ) {
        this.skill = skill;
        this.parent = parent;
        this.document = document;
    }

    public static ProfileSkill create( Profile profile, Skill skill ) {
        /*
        MongoDocument d = createDocument( profile.getIdentifier() );

        d.set( Skill.CERTIFICATE, skill.getIdentifier() );
        d.setList( VALIDATEDBY );

        ProfileSkill e = new ProfileSkill( profile, skill, d );

        return e;
        */
        return null;
    }

    public int getNumberOfValidations() {
        try {
            return getValidationDocuments().size();
        } catch( Exception e ) {
            return 0;
        }
    }

    public void doGetValidations( Request request, Response response ) throws IOException {
        // int number = request.getValue( "number", 10 );
        // int offset = request.getValue( "offset", 0 );

        List<MongoDocument> docs = getValidationDocuments();

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( docs ) );
    }

    public void doRemove( Request request, Response response ) throws NoAuthorizationException {
        response.setRenderType( Response.RenderType.NONE );
        request.checkPermissions( parent.getParent(), ACL.Permission.ADMIN );

        remove();
        response.setStatus( HttpServletResponse.SC_OK );
    }

    public void remove() {
        logger.debug( "Removing skill and validations" );
        /* Remove from profile */
        MongoDBQuery query = new MongoDBQuery().getId( getProfile().getIdentifier() );
        //MongoUpdate pull = new MongoUpdate().pull( "extensions.action.certificates.certificates.skill", skill.getIdentifier() );
        MongoUpdate pull = new MongoUpdate().pull( "extensions.action.skills.skills", "skill", skill.getIdentifier() );
        MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).update( query, pull );

        /* Remove validations */
        removeValidations();
    }

    public void doGetValidations2( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        int number = request.getValue( "number", 10 );
        int offset = request.getValue( "offset", 0 );

        List<String> strings = new ArrayList<String>( number );

        List<MongoDocument> docs = getValidationDocuments();

        for( MongoDocument d : docs ) {
            String pid = d.get( "profile" );
            Profile p = Core.getInstance().getNodeById( this, pid );
            strings.add( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( p, "badge.vm" ) );
        }

        logger.debug( "_-----> " + strings );

        PrintWriter writer = response.getWriter();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        writer.write( gson.toJson( strings ) );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Profile skill";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public MongoDocument getValidationData() {
        MongoDBQuery query = new MongoDBQuery().is( "profile", getProfile().getIdentifier() ).is( Skill.SKILL, skill.getIdentifier() );
        return MongoDBCollection.get( VALIDATIONS_COLLECTION ).findOne( query );
    }

    public List<MongoDocument> getValidationDocuments() {
        MongoDocument doc = getValidationData();

        if( doc != null && !doc.isNull() ) {
            return doc.getList( "validatedby" );
        } else {
            return Collections.emptyList();
        }
    }

    public Validation getLastValidation2() throws ItemInstantiationException, NotFoundException {
        String pid = document.get( "profile", null );
        Date date = document.get( "date", null );
        if( pid != null && date != null ) {
            Profile profile = Profile.getNodeById( this, pid );
            return new Validation( date, profile );
        } else {
            return null;
        }
        /*
        if( getValidationDocuments() != null && getValidationDocuments().size() > 0 ) {
            MongoDocument d = getValidationDocuments().get( getValidationDocuments().size() - 1 );
            Profile validator = (Profile) Core.getInstance().getNodeById( this, (String) d.get( "profile" ) );
            return new Validation( (Date) d.get("date"), validator );
        } else {
            return null;
        }
        */
    }

    public Validation getLastValidation() {
        MongoDocument d = getValidationData();
        if( d != null && !d.isNull() ) {
            List<MongoDocument> docs = d.getList( "validatedby" );
            if( docs.size() > 0 ) {
                MongoDocument v = docs.get( docs.size() - 1 );
                String pid = v.get( "profile", null );
                Date date = v.get( "date", null );
                Profile profile = Profile.getNodeById( this, pid );
                return new Validation( date, profile );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void doValidate( Request request, Response response ) throws NoAuthorizationException, IOException {
        //Profile profile = (Profile) parent.getParent();
        Profile user = (Profile) request.getUser();

        response.setRenderType( Response.RenderType.NONE );
        //request.checkPermissions( (Authorizer) pr, Authorizer.Authorization.MODERATE );
        if( request.isAuthenticated() ) {
            validateSkill( user );
        } else {
            throw new NoAuthorizationException( "You are not authenticated" );
        }

        response.getWriter().write( "BUH YEAH" );
    }

    private void removeValidations() {
        MongoDBQuery query = new MongoDBQuery().is( "profile", getProfile().getIdentifier() ).is( Skill.SKILL, skill.getIdentifier() );
        MongoDBCollection.get( VALIDATIONS_COLLECTION ).remove( query );
    }

    /**
     *
     * @param profile The {@link Profile} validating the {@link Skill}.
     */
    public void validateSkill( Profile profile ) {
        logger.debug( "Validating skill " + skill + " for " + this + " by " + profile );

        /* Update profile, no */
        //document.set( "profile", profile.getIdentifier() );
        //document.set( "date", new Date() );
        //Core.superSave( this );

        /* Update data, add! */
        logger.debug( "Adding data to " + VALIDATIONS_COLLECTION );
        MongoDocument doc = getValidationData();
        if( doc == null || doc.isNull() ) {
            doc = Validation.createNode( getProfile(), skill );
        }

        doc.addToList( "validatedby", Validation.create( profile ) );
        MongoDBCollection.get( VALIDATIONS_COLLECTION ).save( doc );

        /*
        MongoDocument d = new MongoDocument(  ).set( "profile", profile.getIdentifier() ).set( "date", new org.seventyeight.utils.Date() );
        logger.debug( "VALIDATED: " + d );
        document.addToList( VALIDATEDBY, d );
        save();
        */
        //((Profile)parent.getParent().getParent()).save();
        //Core.superSave( this );
    }

    public Profile getProfile() {
        return Core.superGet( parent );
    }

    public Skill getSkill() {
        return skill;
    }

    /**
     * <pre>
     * {
     *     profile: pid,
     *     skill: cid,
     *     validations : [
     *         {
     *             profile: pid,
     *             date: date
     *         } ...
     *     ]
     * }
     * </pre>
     */
    public static class Validation {
        private Date date;
        private Profile profile;

        public Validation( Date date, Profile profile ) {
            this.date = date;
            this.profile = profile;
        }

        public Date getDate() {
            return date;
        }

        public Profile getProfile() {
            return profile;
        }

        public static MongoDocument create( Profile profile ) {
            return new MongoDocument().set( "profile", profile.getIdentifier() ).set( "date", new Date() );
        }

        public static MongoDocument createNode( Profile profile, Skill skill ) {
            MongoDocument d = new MongoDocument().set( "profile", profile.getIdentifier() ).set( Skill.SKILL, skill.getIdentifier() );
            return d;
        }
    }
}
