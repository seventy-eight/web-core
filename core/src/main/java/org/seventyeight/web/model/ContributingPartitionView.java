package org.seventyeight.web.model;

import java.util.Arrays;

/**
 * @author cwolfgang
 */
public class ContributingPartitionView {
    public String viewName;
    public String title;
    public Object partition;

    public ContributingPartitionView( String viewName, String title, Object partition ) {
        this.viewName = viewName;
        this.title = title;
        this.partition = partition;
    }

    public ContributingPartitionView( String viewName, Object partition ) {
        this.viewName = viewName;
        this.title = viewName;
        this.partition = partition;
    }

    public String getViewName() {
        return viewName;
    }

    public String getTitle() {
        return title;
    }

    public Object getPartition() {
        return partition;
    }

    @Override
    public String toString() {
        return partition + "----->" + viewName;
    }
}
