package org.seventyeight.web.extensions;

import org.seventyeight.web.model.ContributingPartitionView;
import org.seventyeight.web.servlet.Request;

import java.util.List;
import java.util.Locale;

/**
 * @author cwolfgang
 */
public interface Partitioned {
    public List<ContributingPartitionView> getPartitions( Locale locale );
    public ContributingPartitionView getActivePartition( Request request );
}
