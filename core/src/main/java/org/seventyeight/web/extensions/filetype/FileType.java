package org.seventyeight.web.extensions.filetype;

import org.seventyeight.web.model.*;

import java.util.List;

/**
 * @author cwolfgang
 */
public abstract class FileType extends Configurable implements ExtensionPoint {
    public abstract List<String> getFileExtensions();
}
