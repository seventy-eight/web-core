package org.seventyeight.web;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class UserAgentTest {

    @Test
    public void test1() {
        String str = "Mozilla/1.22 (compatible; MSIE 2.0; Windows 95)";

        UserAgent agent = UserAgent.getUserAgent( str );

        assertNotNull( agent );
        assertThat(agent.getModel(), is( UserAgent.Model.InternetExplorer));
        assertThat( agent.getVersion(), is("2.0") );
        assertThat( agent.getPlatform(), is( UserAgent.Platform.Windows95) );

        String str2 = "Mozilla/4.0 (compatible; MSIE 6.1; Windows XP)";
        UserAgent agent2 = UserAgent.getUserAgent( str2 );

        assertNotNull( agent2 );
        assertThat(agent2.getModel(), is( UserAgent.Model.InternetExplorer));
        assertThat( agent2.getVersion(), is("6.1") );
        assertThat( agent2.getPlatform(), is( UserAgent.Platform.WindowsXP) );

        String str3 = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.5) Gecko/20041117 Firefox/1.0 (Debian package 1.0-2.0.0.45.linspire0.4))";
        UserAgent agent3 = UserAgent.getUserAgent( str3 );

        assertNotNull( agent3 );
        assertThat(agent3.getModel(), is( UserAgent.Model.Firefox));
        assertThat( agent3.getVersion(), is("1.0") );
        assertThat( agent3.getPlatform(), is( UserAgent.Platform.Debian) );

        String str4 = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0";
        UserAgent agent4 = UserAgent.getUserAgent( str4 );

        assertNotNull( agent4 );
        assertThat(agent4.getModel(), is( UserAgent.Model.Firefox));
        assertThat( agent4.getVersion(), is("24.0") );
        assertThat( agent4.getPlatform(), is( UserAgent.Platform.Ubuntu) );
    }
}
