package org.seventyeight.web.project.model;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ProfileSkills extends Action<ProfileSkills> implements Getable<ProfileSkill> {

    private static Logger logger = LogManager.getLogger( ProfileSkills.class );

    private static SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );

    public ProfileSkills( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public String getDisplayName() {
        return "Profile skills";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public boolean hasSkill( String skillId ) throws NotFoundException {
        logger.debug( "Does " + this + " have " + skillId );

        MongoDocument doc = new MongoDocument().set( "skill", skillId );
        MongoDBQuery query = new MongoDBQuery().elemMatch( ((ActionDescriptor)getDescriptor()).getMongoPath() + "skills", doc ).getId( getProfile().getIdentifier() );
        MongoDocument d = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( query );
        logger.debug( "Found: " + d );
        if( d == null || d.isNull() ) {
            return false;
        } else {
            return true;
        }
    }

    @PostMethod
    public void doAdd( Request request, Response response ) throws ItemInstantiationException, IOException, TemplateException, NoAuthorizationException {
        response.setRenderType( Response.RenderType.NONE );
        request.checkPermissions( parent, ACL.Permission.ADMIN );

        String title = request.getValue( "skillTitle", null );
        logger.debug( "Adding " + title + " to " + this );

        if( title != null ) {
            Skill c = null;
            try {
                c = Skill.getSkillByTitle( title, this );

                if( hasSkill( c.getIdentifier() ) ) {
                    response.sendError( Response.SC_NOT_ACCEPTABLE, this + " already have " + c );
                    return;
                }
            } catch( NotFoundException e ) {
                logger.debug( e );

                c = Skill.createSkill( title );
                c.save();
            }

            String description = request.getValue( "skillDescription", "" );
            String dateString = request.getValue( "received", "" );

            Date d = null;
            try {
                d = format.parse( dateString );
            } catch( ParseException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            addSkill( c, description, d );
            response.setStatus( HttpServletResponse.SC_OK );
        } else {
            logger.debug( "No skill title given" );
            response.setStatus( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    @Override
    public ProfileSkill get( String token ) throws NotFoundException {
        logger.debug( "Get skill " + token );

        /*
        List<MongoDocument> docs = document.getList( Skill.CERTIFICATES );

        MongoDBQuery q1 = new MongoDBQuery().is( ((ExtensionDescriptor)getDescriptor()).getMongoPath() + "certificates.certificate", token );
        MongoDocument fields = new MongoDocument(  ).set( "_id", 0 ).set( ((ExtensionDescriptor)getDescriptor()).getMongoPath() + "certificates.$", 1 );
        MongoDocument d = MongoDBCollection.get( Core.NODE_COLLECTION_NAME ).findOne( q1, fields );
        logger.debug( d );
        */

        //MongoDocument d = ((MongoDocument)document.get( Skill.CERTIFICATES )).getSubDocument( token, null );
        String path = ((ExtensionDescriptor)getDescriptor()).getMongoPath() + Skill.SKILLS;
        logger.debug( "PATH: " + path );
        List<MongoDocument> docs = document.getList( Skill.SKILLS );

        if( docs != null && docs.size() > 0 ) {
            for( MongoDocument d : docs ) {
                if( d.get( "skill", "" ).equals( token ) ) {
                    logger.debug( "Found the profile skill" );
                    try {
                        Skill c = (Skill) Core.getInstance().getNodeById( this, token );
                        return new ProfileSkill( this, c, d );
                    } catch( ItemInstantiationException e ) {
                        throw new NotFoundException( e.getMessage(), "Error while finding", e );
                    }
                }
            }
        } else {
            logger.debug( "D was null" );
        }

        throw new NotFoundException( parent + " does not have the skill " + token );
    }

    public List<ProfileSkill> getSkills() throws NotFoundException, ItemInstantiationException {
        return getSkills( 0, -1 );
    }

    public List<ProfileSkill> getSkills( int offset, int number ) throws ItemInstantiationException, NotFoundException {
        List<MongoDocument> docs = document.getList( Skill.SKILLS );

        if( docs != null && docs.size() > 0 ) {
            List<ProfileSkill> skills = new ArrayList<ProfileSkill>( docs.size() );

            // Todo Do something with offset and limit
            for( MongoDocument d : docs ) {
                Skill c = (Skill) Core.getInstance().getNodeById( this, d.get( Skill.SKILL, "" ) );

                ProfileSkill pc = new ProfileSkill( this, c, d );

                skills.add( pc );
            }

            return skills;
        } else {
            return Collections.emptyList();
        }
    }



    public void doList( Request request, Response response ) throws IOException, TemplateException {
        PrintWriter writer = response.getWriter();
        response.setRenderType( Response.RenderType.NONE );
        // TODO cache
        writer.write( ( Core.getInstance().getTemplateManager().getRenderer( request ).renderObject( this, "list.vm" ) ) );
    }

    public void addSkill( Skill skill, String description, Date received ) {
        logger.debug( "Adding skill " + skill );

        document.addToList( Skill.SKILLS, new MongoDocument().set( Skill.SKILL, skill.getIdentifier() ).set( "added", new Date() ).set( "description", description ).set( "received", received ) );

        ((Profile)parent).save();
    }

    public Profile getProfile() {
        return (Profile) parent;
    }

    @Override
    public void save( CoreRequest request, JsonObject jsonData ) throws ClassNotFoundException, ItemInstantiationException, SavingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static class ProfileSkillsDescriptor extends Action.ActionDescriptor<ProfileSkills> {

        @Override
        public String getDisplayName() {
            return "Profile skills";
        }

        @Override
        public String getExtensionName() {
            return "skills";
        }

        /*
        @Override
        public String getUrlName() {
            return "certificate";
        }
        */

        @Override
        public boolean isApplicable( Node node ) {
            return node instanceof Profile;
        }

        @Override
        public List<Searchable> getSearchables() {
            List<Searchable> ss = new ArrayList<Searchable>( 3 );

            //ss.add( new VerifiedBy() );
            //ss.add( new ValidatedAfter() );
            ss.add( new SkillId() );

            return ss;
        }

        private class HasSkill extends Searchable {

            @Override
            public Class<? extends Node> getClazz() {
                return Profile.class;
            }

            @Override
            public String getName() {
                return "Has skill";
            }

            @Override
            public String getMethodName() {
                return "has-skill";
            }

            @Override
            public MongoDBQuery search( String term ) {
                logger.debug( "Has skill {}", term );
                //query.elemMatch( CERTIFICATES, (MongoDocument) new MongoDocument().set( CERTIFICATE, term ) );
                return new MongoDBQuery().is( getMongoPath() + Skill.SKILLS + "." + Skill.SKILL, term );
            }
        }

        private class SkillId extends Searchable {

            @Override
            public Class<? extends Node> getClazz() {
                return Profile.class;
            }

            @Override
            public String getName() {
                return "Skill id";
            }

            @Override
            public String getMethodName() {
                return "skill-id";
            }

            @Override
            public MongoDBQuery search( String term ) {
                logger.debug( "SEARCHING FOR " + term );
                //query.elemMatch( CERTIFICATES, (MongoDocument) new MongoDocument().set( CERTIFICATE, term ) );
                return new MongoDBQuery().is( getMongoPath() + Skill.SKILLS + "." + Skill.SKILL, term );
            }
        }

        private class ValidatedAfter extends Searchable {

            private final SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );

            @Override
            public Class<? extends Node> getClazz() {
                return Profile.class;
            }

            @Override
            public String getName() {
                return "Validated after";
            }

            @Override
            public String getMethodName() {
                return "validated-after";
            }

            @Override
            public MongoDBQuery search( String term ) {
                try {
                    Date date = format.parse( term );
                    return new MongoDBQuery().greaterThan( "extensions.action.skills.skills.date", date );
                } catch( ParseException e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return new MongoDBQuery();
            }
        }

        private class VerifiedBy extends Searchable {

            @Override
            public Class<? extends Node> getClazz() {
                return Profile.class;
            }

            @Override
            public String getName() {
                return "Verified by";
            }

            @Override
            public String getMethodName() {
                return "verified-by";
            }

            @Override
            public MongoDBQuery search( String term ) {
                //query.is(  )
                return new MongoDBQuery();
            }
        }
    }
}
