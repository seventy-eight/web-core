package org.seventyeight.web;

import org.seventyeight.web.Core;
import org.seventyeight.web.actions.*;
import org.seventyeight.web.extensions.filetype.ImageFileType;
import org.seventyeight.web.extensions.footer.Footer;
import org.seventyeight.web.model.Action;
import org.seventyeight.web.model.Menu;
import org.seventyeight.web.nodes.*;
import org.seventyeight.web.project.actions.AddCertificate;
import org.seventyeight.web.project.actions.AddNode;
import org.seventyeight.web.project.actions.CertificateSearch;
import org.seventyeight.web.project.actions.Search;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.Profile;
import org.seventyeight.web.project.model.Role;

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

        /* Adding search action */
        Search search = new Search();
        CertificateSearch cs = new CertificateSearch( search );
        NodeSearch ns = new NodeSearch( search );

        search.addAction( cs );
        search.addAction( ns );

        actions.add( search );

        /* Adders */
        AddNode add = new AddNode();

        AddCertificate ac = new AddCertificate( add );

        add.addAction( ac );

        actions.add( add );

        /**/
        addDescriptor( new Profile.ProfileDescriptor() );
        addDescriptor( new Group.GroupDescriptor() );
        addDescriptor( new FileNode.FileDescriptor() );
        addDescriptor( new Certificate.CertificateDescriptor() );

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
