package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.actions.*;
import org.seventyeight.web.extensions.GravatarPortrait;
import org.seventyeight.web.extensions.TabbedPartitionedResource;
import org.seventyeight.web.extensions.UploadablePortrait;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.extensions.searchers.TitleSearch;
import org.seventyeight.web.extensions.searchers.TypeSearch;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.nodes.*;
import org.seventyeight.web.project.actions.AddNode;
import org.seventyeight.web.project.actions.CertificateSearch;
import org.seventyeight.web.project.model.*;

import java.io.File;

/**
 * @author cwolfgang
 */
public class ProjectCore extends Core {

    private static Logger logger = LogManager.getLogger( ProjectCore.class );

    private File signaturePath;

    public ProjectCore( File path, String dbname ) throws CoreException {
        super( path, dbname );

        signaturePath = new File( path, "signatures" );

        /* Mandatory top level Actions */
        children.put( "static", new StaticFiles() );
        children.put( "theme", new ThemeFiles() );
        children.put( "new", new NewContent( this ) );
        //children.put( "get", new Get( this ) );
        children.put( "upload", new Upload() );
        children.put( "nodes", new Nodes() );
        children.put( "configuration", new GlobalConfiguration() );
        children.put( "search2", new org.seventyeight.web.actions.Search() );

        children.put( "information", new Information() );

        children.put( "profiles", new Profiles() );
        children.put( "language", new LanguageAction() );

        //children.put( "login", new Login( this ) );

        /* Adding search action */
        Search search = new Search();
        CertificateSearch cs = new CertificateSearch( search );
        NodeSearch ns = new NodeSearch( search );

        //search.addAction( Skill.CERTIFICATE, cs );
        //search.addAction( "node", ns );

        children.put( "search", search );

        /* Adders */
        AddNode add = new AddNode();

        //AddCertificate ac = new AddCertificate( add );

        //add.addAction( Skill.CERTIFICATE, ac );

        children.put( "add", add );

        /**/
        addDescriptor( new Profile.ProfileDescriptor() );
        addDescriptor( new Company.CompanyDescriptor() );
        addDescriptor( new Project.ProjectDescriptor() );
        //addDescriptor( new Group.GroupDescriptor() );
        addDescriptor( new Role.RoleDescriptor() );
        addDescriptor( new FileResource.FileDescriptor() );

        addDescriptor( new Skill.SkillDescriptor() );

        addDescriptor( new Topic.TopicDescriptor() );

        addDescriptor( new ImageFileType.ImageFileTypeDescriptor() );

        addDescriptor( new Collection.CollectionDescriptor() );

        addDescriptor( new Signature.SignatureDescriptor() );
        addDescriptor( new ProfileSkills.ProfileSkillsDescriptor() );
        addDescriptor( new ProfileCompanies.ProfileCompaniesDescriptor() );

        addDescriptor( new GetAction.GetDescriptor() );

        addSearchable( new TitleSearch() );
        addSearchable( new TypeSearch() );

        addDescriptor( new GravatarPortrait.GravatarPortraitDescriptor() );
        addDescriptor( new UploadablePortrait.UploadablePortraitDescriptor() );

        addExtension( new TabbedPartitionedResource() );

        //addDescriptor( new  );

        //addExtension( ImageFileType.class, new ImageFileType(  ) );

        mainMenu.add( new Menu.MenuItem( "New Content", "/new/" ) );
        mainMenu.add( new Menu.MenuItem( "Upload", "/upload/" ) );
        mainMenu.add( new Menu.MenuItem( "Configure", "/configuration/" ) );
        mainMenu.add( new Menu.MenuItem( "Search", "/search2/" ) );


        /*
        MongoDBQuery query = new MongoDBQuery().is( "username", "anonymous" );
        MongoDocument d = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( query );
        Profile a = getItem( this, d );
        setAnonymous( a );
        */
    }

    public File getSignaturePath() {
        return signaturePath;
    }

    public String getSignatureURL() {
        return "signatures/";
    }

    @Override
    public void save() {
        logger.fatal( "SAVING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
    }

    @Override
    public String getDefaultTemplate() {
        return "org/seventyeight/web/project/main.vm";
    }
}
