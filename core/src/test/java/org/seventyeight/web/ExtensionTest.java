package org.seventyeight.web;

import org.junit.ClassRule;
import org.junit.Test;
import org.seventyeight.web.model.NotFoundException;

/**
 * @author cwolfgang
 */
public class ExtensionTest {

    @ClassRule
    public static DummyCoreEnvironment env = new DummyCoreEnvironment( "coreTest" );

    @Test
    public void test01() throws NotFoundException {

    }
}
