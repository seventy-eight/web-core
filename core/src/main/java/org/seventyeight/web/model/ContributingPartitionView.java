package org.seventyeight.web.model;

import java.util.Arrays;

/**
 * @author cwolfgang
 */
public class ContributingPartitionView {
    public String[] viewNames;
    public String[] titles;
    public Object partition;

    public ContributingPartitionView( String[] viewNames, String[] titles, Object partition ) {
        this.viewNames = viewNames;
        this.titles = titles;
        this.partition = partition;
    }

    public ContributingPartitionView( String[] viewNames, Object partition ) {
        this.viewNames = viewNames;
        this.titles = viewNames;
        this.partition = partition;
    }

    @Override
    public String toString() {
        return partition + "----->" + Arrays.asList(viewNames);
    }
}
