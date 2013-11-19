package org.seventyeight.web.project.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Skill extends Resource<Skill> {

    private static Logger logger = LogManager.getLogger( Skill.class );

    public static final String SKILL = "skill";
    public static final String SKILLS = "skills";
    public static final String SKILL_NAME = "Skill";

    public static final String SKILL_DOTTED = SKILLS + "." + SKILL;

    public Skill( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public String getDisplayName() {
        return getTitle();
    }

    @Override
    public String getMainTemplate() {
        return Core.MAIN_TEMPLATE;
    }

    @Override
    public String getPortrait() {
        return "/theme/framed-certificate-small.png";
    }

    public static Skill createSkill( String skillName ) throws ItemInstantiationException {
        Skill skill = (Skill) ((SkillDescriptor)Core.getInstance().getDescriptor( Skill.class )).newInstance( skillName );
        return skill;
    }

    public List<Profile> getProfiles() {
        logger.debug( "Getting members for " + this );

        MongoDBQuery q = new MongoDBQuery().is( "extensions.action.skills.skills." + SKILL, getIdentifier() );
        List<MongoDocument> docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).find( q );

        logger.debug( "DOCS ARE: " + docs );

        List<Profile> users = new ArrayList<Profile>( docs.size() );

        for( MongoDocument d : docs ) {
            users.add( new Profile( this, d ) );
        }

        return users;
    }

    public boolean hasSkill( Profile profile ) {
        MongoDBQuery q = new MongoDBQuery().is( "extensions.action.skills.skills." + SKILL, getIdentifier() ).getId( profile.getIdentifier() );
        MongoDocument docs = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( q );

        return ( docs != null && !docs.isNull() );
    }

    /*
    public void doAddCertified( Request request, Response response ) throws IOException, TemplateException {
        String profileName = request.getValue( "profile", null );
        logger.debug( "Adding " + profileName + " as certified" );

        if( profileName != null ) {
            Profile profile = Profile.getProfileByUsername( this, profileName );
            if( profile != null ) {
                profile.addSkill( this );
                response.sendRedirect( "" );
                return;
            } else {
                Response.HttpCode c = new Response.HttpCode( 404, "Profile not found", "The profile " + profileName + " was not found" );
                c.render( request, response );
            }
        } else {
            Response.HttpCode c = new Response.HttpCode( 404, "Not provided", "No profile provided" );
            c.render( request, response );
        }

    }
    */

    /**
     * Get a {@link Skill} by its title
     */
    public static Skill getSkillByTitle( String title, Node parent ) throws NotFoundException {
        MongoDocument doc = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( new MongoDBQuery().is( "title", title ).is( "type", SKILL ) );
        if( !doc.isNull() ) {
            return new Skill( parent, doc );
        } else {
            throw new NotFoundException( "The skill \"" + title + "\" was not found" );
        }
    }

    public static class SkillDescriptor extends ResourceDescriptor<Skill> {

        @Override
        public String getType() {
            return SKILL;
        }

        @Override
        public String getDisplayName() {
            return SKILL_NAME;
        }

        @Override
        public boolean allowIdenticalNaming() {
            return false;
        }

        /*
        @Override
        public Node getChild( String name ) throws NotFoundException {
            Skill cert = getSkillByTitle( name, this );
            if( cert != null ) {
                return cert;
            } else {
                throw new NotFoundException( "The certificate " + cert + " was not found" );
            }
        }
        */


    }
}
