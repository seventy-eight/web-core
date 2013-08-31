package org.seventyeight.web.project.model;

import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.project.ProjectEnvironment;

/**
 * @author cwolfgang
 */
public class ProfileTest {

    @Rule
    public ProjectEnvironment env = new ProjectEnvironment( "seventyeight-profile-test" );

    @Test
    public void createProfileTest() throws ItemInstantiationException {

        Profile profile = Profile.createProfile( "profile1", "Wolle", "Bolle", "wolle@bolle.dk", "pass" );
    }
}
