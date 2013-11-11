package org.seventyeight.web.extensions;

import org.seventyeight.web.model.ContributingPartitionView;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface PartitionContributor {
    public void insertContributions( List<ContributingPartitionView> partitions );
}
