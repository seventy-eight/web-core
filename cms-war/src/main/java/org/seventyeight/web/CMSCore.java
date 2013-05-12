package org.seventyeight.web;

import org.seventyeight.web.Core;

import java.io.File;

/**
 * @author cwolfgang
 */
public class CMSCore extends Core {
    public CMSCore( File path, String dbname ) throws CoreException {
        super( path, dbname );
    }
}
