package org.seventyeight.web.extensions;

import org.seventyeight.web.servlet.Request;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface Partitioned {
    public List<String> getPartitions();
    public String getActivePartition( Request request );
}
