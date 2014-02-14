package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.actions.*;
import org.seventyeight.web.extensions.GravatarPortrait;
import org.seventyeight.web.extensions.TabbedPartitionedResource;
import org.seventyeight.web.extensions.UploadablePortrait;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.extensions.search.CollectionFormatter;
import org.seventyeight.web.extensions.searchers.TitleSearch;
import org.seventyeight.web.extensions.searchers.TypeSearch;
import org.seventyeight.web.model.Comment;
import org.seventyeight.web.model.Menu;
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
        children.put( "search2", new org.seventyeight.web.actions.Search() );

        children.put( "resources", new ResourcesAction() );

        WidgetAction widgets = new WidgetAction();
        children.put( "widgets", widgets );


        children.put( "information", new Information() );

        children.put( "language", new LanguageAction() );

        addDescriptor( new Comment.CommentDescriptor() );

        addDescriptor( new User.UserDescriptor() );

        addDescriptor( new FileResource.FileDescriptor() );

        addDescriptor( new Topic.TopicDescriptor() );

        addDescriptor( new Collection.CollectionDescriptor() );

        addDescriptor( new GetAction.GetDescriptor() );

        addSearchable( new TitleSearch() );
        addSearchable( new TypeSearch() );

        addDescriptor( new GravatarPortrait.GravatarPortraitDescriptor() );
        addDescriptor( new UploadablePortrait.UploadablePortraitDescriptor() );

        addExtension( new TabbedPartitionedResource() );

        addExtension( new ActivityWidget() );

        addExtension( new FileTypeListener() );
        addExtension( new ImageFileType() );

        addExtension( new SearchFormatListener() );
        //addExtension( new CollectionFormatter() );
        addExtension( new WidgetListener() );

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
        return "org/seventyeight/web/main.vm";
    }
}
