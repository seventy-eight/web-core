package org.seventyeight.web.project.model;

import org.junit.Rule;
import org.junit.Test;
import org.seventyeight.database.DatabaseException;
import org.seventyeight.web.WebCoreEnv;
import org.seventyeight.web.project.ProjectEnvironment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author cwolfgang
 */
public class CertificateTest {

    @Rule
    public ProjectEnvironment env = new ProjectEnvironment( "seventyeight-certificate-test" );

    @Test
    public void testBasic() throws DatabaseException {
        Profile owner = env.createProfile( "wolfgang" );
        Profile profile1 = env.createProfile( "wolle" );

        Certificate cert1 = env.createCertificate( "Cert 1", owner );

        profile1.addCertificate( cert1 );

        assertThat( cert1.getProfiles().size(), is( 1 ) );
        assertThat( cert1.getProfiles().get( 0 ), is( profile1 ) );
    }

    @Test
    public void testMultiple() throws DatabaseException {
        Profile owner = env.createProfile( "wolfgang" );
        Profile user1 = env.createProfile( "user1" );
        Profile user2 = env.createProfile( "user2" );

        Certificate cert1 = env.createCertificate( "Cert 1", owner );

        user1.addCertificate( cert1 );
        user2.addCertificate( cert1 );

        assertThat( cert1.getProfiles().size(), is( 2 ) );
        assertThat( cert1.getProfiles().get( 0 ), is( user1 ) );
        assertThat( cert1.getProfiles().get( 1 ), is( user2 ) );
    }

    @Test
    public void testValidate() throws DatabaseException {
        Profile owner = env.createProfile( "wolfgang" );
        Profile profile1 = env.createProfile( "wolle" );

        Certificate cert1 = env.createCertificate( "Cert 1", owner );

        profile1.addCertificate( cert1 );

        assertThat( cert1.getProfiles().size(), is( 1 ) );
        assertThat( cert1.getProfiles().get( 0 ), is( profile1 ) );

        profile1.validateCertificate( cert1, owner );

        Profile profile1_new = Profile.getProfileByUsername( owner, "wolle" );
        System.out.println( "NEW: " + profile1_new.getDocument() );

    }
}
