package org.seventyeight.web.extensions;

import org.seventyeight.web.model.ContributingPartitionView;
import org.seventyeight.web.servlet.Request;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface Partitioned {
    public List<ContributingPartitionView> getPartitions();
    public ContributingPartitionView getActivePartition( Request request );
}
