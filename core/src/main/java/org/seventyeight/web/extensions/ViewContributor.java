package org.seventyeight.web.extensions;

import org.seventyeight.web.model.ContributingView;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface ViewContributor {
    public void addContributingViews( List<ContributingView> views );
}
