package org.seventyeight.web.extensions.filetype;

import java.util.Collections;
import java.util.List;

/**
 * @author cwolfgang
 */
public class DefaultFileType extends FileType {
    @Override
    public List<String> getFileExtensions() {
        return Collections.emptyList();
    }
}
