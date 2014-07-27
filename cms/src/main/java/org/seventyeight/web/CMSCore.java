package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.actions.*;
import org.seventyeight.web.authorization.BasicResourceBasedSecurity;
import org.seventyeight.web.authorization.PublicACL;
import org.seventyeight.web.extensions.*;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.extensions.listernes.ActivityNodeListener;
import org.seventyeight.web.extensions.searchers.TitleSearch;
import org.seventyeight.web.extensions.searchers.TypeSearch;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.RootNode;
import org.seventyeight.web.music.Artist;
import org.seventyeight.web.music.Concert;
import org.seventyeight.web.music.Festival;
import org.seventyeight.web.music.Venue;
import org.seventyeight.web.nodes.*;
import org.seventyeight.web.nodes.listeners.FileTypeListener;
import org.seventyeight.web.nodes.listeners.SearchFormatListener;
import org.seventyeight.web.nodes.listeners.WidgetListener;
import org.seventyeight.web.social.Follow;
import org.seventyeight.web.widgets.ActivityWidget;
import org.seventyeight.web.widgets.LastUsersWidget;
import org.seventyeight.web.widgets.NextEventWidget;

import java.io.File;

/**
 * @author cwolfgang
 */
public class CMSCore extends Core {

    private static Logger logger = LogManager.getLogger( CMSCore.class );

    private File signaturePath;

    public CMSCore( RootNode root, File path, String dbname ) throws CoreException {
        super( root, path, dbname );

        signaturePath = new File( path, "signatures" );

        /* Mandatory top level Actions */
        root.addNode( "static", new StaticFiles(this) );
        root.addNode( "theme", new ThemeFiles(this) );
        root.addNode( "new", new NewContent( this, root ) );
        //children.put( "get", new Get( this ) );
        root.addNode( "upload", new Upload(this) );
        root.addNode( "configuration", new GlobalConfiguration(this) );
        root.addNode( "search", new org.seventyeight.web.actions.Search(this) );

        root.addNode( "resources", new ResourcesAction(this) );

        WidgetAction widgets = new WidgetAction(this);
        root.addNode( "widgets", widgets );


        root.addNode( "information", new Information(this) );

        root.addNode( "language", new LanguageAction(this) );

        addDescriptor( new Comment.CommentDescriptor(this) );

        addDescriptor( new User.UserDescriptor(this) );

        addDescriptor( new Group.GroupDescriptor(this) );

        addDescriptor( new FileResource.FileDescriptor(this) );

        addDescriptor( new Tags.TagsDescriptor(this) );
        addDescriptor( new Event.EventDescriptor(this)   );

        addDescriptor( new Topic.TopicDescriptor(this) );

        addDescriptor( new Collection.CollectionDescriptor(this) );

        addDescriptor( new ImageUploadsWrapper.ImageUploadsWrapperDescriptor(this) );

        addDescriptor( new GetAction.GetDescriptor(this) );

        addSearchable( new TitleSearch() );
        addSearchable( new TypeSearch() );

        addDescriptor( new GravatarPortrait.GravatarPortraitDescriptor(this) );
        addDescriptor( new UploadablePortrait.UploadablePortraitDescriptor(this) );

        addExtension( new TabbedPartitionedResource() );
        addExtension( new CollectionExtension() );

        addExtension( new ActivityWidget(this) );

        addExtension( new FileTypeListener(this) );
        addExtension( new ImageFileType() );

        addExtension( new SearchFormatListener(this) );
        //addExtension( new CollectionFormatter() );
        addExtension( new WidgetListener(this) );
        addExtension( new NextEventWidget(this) );
        addExtension( new LastUsersWidget(this) );

        addExtension( new ActivityNodeListener() );
        addExtension( new DefaultMenuContributor() );

        addDescriptor( new BasicResourceBasedSecurity.BasicResourceBasedSecurityDescriptor(this) );
        addDescriptor( new PublicACL.PublicACLDescriptor(this) );

        addDescriptor( new Artist.ArtistDescriptor(this) );
        addDescriptor( new Venue.VenueDescriptor(this) );
        addDescriptor( new Concert.ConcertDescriptor(this) );
        addDescriptor( new Festival.FestivalDescriptor(this) );

        addDescriptor( new Follow.FollowDescriptor(this) );

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
    public String getDefaultTemplate() {
        return "org/seventyeight/web/main.vm";
    }
}
