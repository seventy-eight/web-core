package org.seventyeight.web.model;

import org.junit.Test;
import org.seventyeight.web.Core;
import org.seventyeight.web.DummyCore;
import org.seventyeight.web.Root;
import org.seventyeight.web.actions.ResourcesAction;
import org.seventyeight.web.nodes.User;

/**
 * @author cwolfgang
 */
public class DeletingParentTest {

    @Test
    public void test() {
        Root root = new Root();
        ResourcesAction resourcesAction = new ResourcesAction( null );
        User user = new User( null, resourcesAction, null );
    }
}
