package org.seventyeight.web.project.model;

import org.apache.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.database.mongodb.MongoUpdate;
import org.seventyeight.utils.Date;
import org.seventyeight.utils.Utils;
import org.seventyeight.web.Core;
import org.seventyeight.web.extensions.Partitioned;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.*;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Profile extends User implements Partitioned {

    private static Logger logger = Logger.getLogger( Profile.class );

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";

    public Profile( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new ProfileSaver( this, request );
    }

    public class ProfileSaver extends UserSaver {
        public ProfileSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            super.save();

            logger.debug( "Saving profile" );

            set( FIRST_NAME );
            set( LAST_NAME );
        }
    }

    public static Profile createProfile( String username, String firstName, String lastName, String email, String password ) throws ItemInstantiationException {
        logger.debug( "Creating new profile, " + username );
        if( password == null || firstName == null || lastName == null || email == null ) {
            throw new IllegalStateException( "Not all options are set" );
        }

        ProfileDescriptor d = Core.getInstance().getDescriptor( Profile.class );
        Profile profile = (Profile) d.newInstance( username );

        logger.debug( "Setting mandatory fields" );
        profile.setMandatoryFields( profile );
        logger.debug( "Mandatory fields set" );

        profile.getDocument().set( USERNAME, username );
        profile.getDocument().set( FIRST_NAME, firstName );
        profile.getDocument().set( LAST_NAME, lastName );
        profile.getDocument().set( EMAIL, email );
        try {
            profile.getDocument().set( PASSWORD, Utils.md5( password ) );
        } catch( NoSuchAlgorithmException e ) {
            /* Algorithm must be present */
            throw new IllegalStateException( e );
        }

        return profile;
    }

    public static Profile getProfileByUsername( Node parent, String username ) {
        List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( new MongoDBQuery().is( "username", username ), 0, 1 );

        if( docs != null && !docs.isEmpty() ) {
            return new Profile( parent, docs.get( 0 ) );
        } else {
            logger.debug( "The user " + username + " was not found" );
            return null;
        }
    }

    /*
    @Override
    public Node getChild( String name ) {
        if( name.equalsIgnoreCase( "signature" ) ) {
            MongoDocument doc = document.get( "signature", new MongoDocument() );
            return new Signature( this, doc );
        } else {
            return super.getChild( name );
        }
    }
    */

    @Override
    public String toString() {
        return "Profile[" + getDisplayName() + "]";
    }

    public void doListSkills( Request request, Response response ) throws TemplateException {
        logger.debug( "DO LIST BEFORE" );
        Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "listSkills.vm" );
        logger.debug( "DO LIST AFTER" );
    }

    @Override
    public String getDisplayName() {
        return document.get( FIRST_NAME ) + " " + document.get( LAST_NAME );
    }

    public static Profile getProfileByUsername( String username, Node parent ) throws NotFoundException {
        MongoDocument doc = User.getUserDocumentByUsername( username );
        if( doc != null ) {
            return new Profile( parent, doc );
        } else {
            throw new NotFoundException( "The profile " + username + " was not found", "Profile not found" );
            //throw new NotFoundException( "" ).setHeader( "" );
        }
    }

    public void addRole( Role role ) {
        role.addMember( this );
    }

    public void getLastValidation() {

    }

    @Override
    public List<String> getPartitions() {
        List<String> parts = new ArrayList<String>( 4 );
        parts.add( "Profile" );
        parts.add( "Companies" );
        parts.add( "Project" );
        parts.add( "Certificates" );
        return parts;
    }

    @Override
    public String getActivePartition() {
        return "Profile";
    }

    public void validateSkill( Skill skill, Profile profile ) {
        logger.debug( "Validating skill " + skill + " for " + this + " by " + profile );

        MongoDocument d = new MongoDocument().set( "profile", profile.getIdentifier() ).set( "date", new Date() );

        MongoDBQuery q = new MongoDBQuery().is( Skill.SKILL_DOTTED, skill.getIdentifier() ).getId( this.getIdentifier() );
        //MongoUpdate u = new MongoUpdate().set( Skill.CERTIFICATES + ".$.validatedby", d );
        MongoUpdate u = new MongoUpdate().push( Skill.SKILLS + ".$.validatedby", d );
        MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).update( q, u );
    }

    public static class ProfileCreator {
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String password;

        private Profile profile;

        public ProfileCreator( String username ) {
            this.username = username;
        }

        public ProfileCreator setFirstName( String firstName ) {
            this.firstName = firstName;
            return this;
        }

        public ProfileCreator setLastName( String lastName ) {
            this.lastName = lastName;
            return this;
        }

        public ProfileCreator setEmail( String email ) {
            this.email = email;
            return this;
        }

        public ProfileCreator setMaskedPassword( String password ) {
            this.password = password;
            return this;
        }

        public ProfileCreator setUnmaskedPassword( String password ) throws NoSuchAlgorithmException {
            this.password = Utils.md5( password );
            return this;
        }

        public ProfileCreator create() throws ItemInstantiationException {
            if( password == null || firstName == null || lastName == null || email == null ) {
                throw new IllegalStateException( "Not all " );
            }
            ProfileDescriptor d = Core.getInstance().getDescriptor( Profile.class );
            profile = (Profile) d.newInstance( username );
            return this;
        }

        public Profile getProfile() {
            return profile;
        }

    }

    public static class ProfileDescriptor extends UserDescriptor {

        @Override
        public String getDisplayName() {
            return "Profile";
        }

        @Override
        public String getType() {
            return "profile";
        }

        @Override
        public void save( Request request, Response response ) {
            /**/
        }
    }
}
