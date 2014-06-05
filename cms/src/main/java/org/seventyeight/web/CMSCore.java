package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.actions.*;
import org.seventyeight.web.authorization.ACL;
import org.seventyeight.web.authorization.BasicResourceBasedSecurity;
import org.seventyeight.web.authorization.PublicACL;
import org.seventyeight.web.extensions.*;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.extensions.listernes.ActivityNodeListener;
import org.seventyeight.web.extensions.search.CollectionFormatter;
import org.seventyeight.web.extensions.searchers.TitleSearch;
import org.seventyeight.web.extensions.searchers.TypeSearch;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.music.Artist;
import org.seventyeight.web.music.Concert;
import org.seventyeight.web.music.Festival;
import org.seventyeight.web.music.Venue;
import org.seventyeight.web.nodes.*;
import org.seventyeight.web.nodes.listeners.FileTypeListener;
import org.seventyeight.web.nodes.listeners.SearchFormatListener;
import org.seventyeight.web.nodes.listeners.WidgetListener;
import org.seventyeight.web.widgets.ActivityWidget;

import java.io.File;

/**
 * @author cwolfgang
 */
public class CMSCore extends Core {

    private static Logger logger = LogManager.getLogger( CMSCore.class );

    private File signaturePath;

    public CMSCore( File path, String dbname ) throws CoreException {
        super( path, dbname );

        signaturePath = new File( path, "signatures" );

        /* Mandatory top level Actions */
        children.put( "static", new StaticFiles() );
        children.put( "theme", new ThemeFiles() );
        children.put( "new", new NewContent( this ) );
        //children.put( "get", new Get( this ) );
        children.put( "upload", new Upload() );
        children.put( "configuration", new GlobalConfiguration() );
        children.put( "search", new org.seventyeight.web.actions.Search() );

        children.put( "resources", new ResourcesAction() );

        WidgetAction widgets = new WidgetAction();
        children.put( "widgets", widgets );


        children.put( "information", new Information() );

        children.put( "language", new LanguageAction() );

        addDescriptor( new Comment.CommentDescriptor() );

        addDescriptor( new User.UserDescriptor() );

        addDescriptor( new Group.GroupDescriptor() );

        addDescriptor( new FileResource.FileDescriptor() );

        addDescriptor( new Tags.TagsDescriptor() );
        addDescriptor( new Event.EventDescriptor()   );

        addDescriptor( new Topic.TopicDescriptor() );

        addDescriptor( new Collection.CollectionDescriptor() );

        addDescriptor( new GetAction.GetDescriptor() );

        addSearchable( new TitleSearch() );
        addSearchable( new TypeSearch() );

        addDescriptor( new GravatarPortrait.GravatarPortraitDescriptor() );
        addDescriptor( new UploadablePortrait.UploadablePortraitDescriptor() );

        addExtension( new TabbedPartitionedResource() );
        addExtension( new CollectionExtension() );

        addExtension( new ActivityWidget() );

        addExtension( new FileTypeListener() );
        addExtension( new ImageFileType() );

        addExtension( new SearchFormatListener() );
        //addExtension( new CollectionFormatter() );
        addExtension( new WidgetListener() );

        addExtension( new ActivityNodeListener() );
        addExtension( new DefaultMenuContributor() );

        addDescriptor( new BasicResourceBasedSecurity.BasicResourceBasedSecurityDescriptor() );
        addDescriptor( new PublicACL.PublicACLDescriptor() );

        addDescriptor( new Artist.ArtistDescriptor() );
        addDescriptor( new Venue.VenueDescriptor() );
        addDescriptor( new Concert.ConcertDescriptor() );
        addDescriptor( new Festival.FestivalDescriptor() );

        //addDescriptor( new  );

        //addExtension( ImageFileType.class, new ImageFileType(  ) );

        /*
        mainMenu.addItem( "Main", new Menu.MenuItem( "New Content", "/new/", ACL.Permission.READ ) );
        mainMenu.addItem( "Main", new Menu.MenuItem( "Upload", "/upload/", ACL.Permission.READ ) );
        mainMenu.addItem( "Main", new Menu.MenuItem( "Configure", "/configuration/", ACL.Permission.ADMIN ) );
        mainMenu.addItem( "Main", new Menu.MenuItem( "Search", "/search2/", ACL.Permission.ALL ) );
        */

        /*
        MongoDBQuery query = new MongoDBQuery().is( "username", "anonymous" );
        MongoDocument d = MongoDBCollection.get( Core.RESOURCES_COLLECTION_NAME ).findOne( query );
        Profile a = getNode( this, d );
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
        return "org/seventyeight/web/main.vm";
    }
}
