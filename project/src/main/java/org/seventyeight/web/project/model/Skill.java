package org.seventyeight.web.project.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.structure.Tuple;
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

        public void doSearch( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException {
            int offset = request.getInteger( "offset", 0 );
            int number = request.getInteger( "number", 10 );
            String term = request.getValue( "term", null );

            logger.debug( term + ", OFFSET: " + offset + ", NUMBER: " + number );

            response.setRenderType( Response.RenderType.NONE );

            if( term == null || term.isEmpty() ) {
                response.getWriter().print( "{}" );
            } else {
                MongoDBQuery dbquery = new MongoDBQuery().regex( "title", "(?i)" + term + ".*" ).is( "type", "skill" );
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
