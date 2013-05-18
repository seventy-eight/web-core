package org.seventyeight.web;

import org.seventyeight.web.Core;
import org.seventyeight.web.actions.*;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.extensions.footer.Footer;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.nodes.FileNode;
import org.seventyeight.web.nodes.StaticFiles;
import org.seventyeight.web.nodes.ThemeFiles;
import org.seventyeight.web.nodes.User;
import org.seventyeight.web.project.model.Profile;

import java.io.File;

/**
 * @author cwolfgang
 */
public class ProjectCore extends Core {

    public ProjectCore( File path, String dbname ) throws CoreException {
        super( path, dbname );

        /* Mandatory top level Actions */
        actions.add( new StaticFiles() );
        actions.add( new ThemeFiles() );
        actions.add( new NewContent( this ) );
        actions.add( new Get( this ) );
        actions.add( new Upload() );
        actions.add( new Nodes() );
        actions.add( new GlobalConfiguration() );

        /**/
        addDescriptor( new Profile.ProfileDescriptor() );
        addDescriptor( new FileNode.FileDescriptor() );

        addDescriptor( new ImageFileType.ImageFileTypeDescriptor() );
        //addDescriptor( new  );

        //addExtension( ImageFileType.class, new ImageFileType(  ) );

        /* test */
        addDescriptor( new Footer.FooterDescriptor() );

        mainMenu.add( new Menu.MenuItem( "New Content", "/new/" ) );
        mainMenu.add( new Menu.MenuItem( "Upload", "/upload/" ) );
        mainMenu.add( new Menu.MenuItem( "Test", "/user/wolle/" ) );
    }

}
