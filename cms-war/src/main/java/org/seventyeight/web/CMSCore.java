package org.seventyeight.web;

import org.seventyeight.web.actions.*;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.nodes.FileResource;
import org.seventyeight.web.nodes.StaticFiles;
import org.seventyeight.web.nodes.ThemeFiles;
import org.seventyeight.web.nodes.User;

import java.io.File;

/**
 * @author cwolfgang
 */
public class CMSCore extends Core {

    public CMSCore( File path, String dbname ) throws CoreException {
        super( path, dbname );

        /* Mandatory top level Actions */
        children.put( "static", new StaticFiles() );
        children.put( "theme", new ThemeFiles() );
        children.put( "new", new NewContent( this ) );
        children.put( "get", new Get( this ) );
        children.put( "upload", new Upload() );
        children.put( "nodes", new Nodes() );
        children.put( "configuration", new GlobalConfiguration() );

        /**/
        addDescriptor( new User.UserDescriptor() );
        addDescriptor( new FileResource.FileDescriptor() );

        addDescriptor( new ImageFileType.ImageFileTypeDescriptor() );
        //addDescriptor( new  );

        //addExtension( ImageFileType.class, new ImageFileType(  ) );

        mainMenu.add( new Menu.MenuItem( "New Content", "/new/" ) );
        mainMenu.add( new Menu.MenuItem( "Upload", "/upload/" ) );
        mainMenu.add( new Menu.MenuItem( "Test", "/user/wolle/" ) );
    }

    @Override
    public void save() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
