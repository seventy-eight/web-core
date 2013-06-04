package org.seventyeight.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class TimeUtilsTest {

    @Test
    public void testOneSecond() {
        TimeUtils.Time t = TimeUtils.getTime( 1000 );

        assertThat( t.seconds, is( 1 ) );
    }

    @Test
    public void testOneHour() {
        TimeUtils.Time t = TimeUtils.getTime( TimeUtils.HOURS );

        assertThat( t.seconds, is( 0 ) );
        assertThat( t.hours, is( 1 ) );
    }

    @Test
    public void testMulti() {
        TimeUtils.Time t = TimeUtils.getTime( TimeUtils.HOURS * 2 + TimeUtils.MINUTES * 33 + TimeUtils.SECONDS * 13 );

        assertThat( t.millis, is( 0 ) );
        assertThat( t.seconds, is( 13 ) );
        assertThat( t.minutes, is( 33 ) );
        assertThat( t.hours, is( 2 ) );
        assertThat( t.days, is( 0 ) );
    }

    @Test
    public void TestSmallString() {
        long t = TimeUtils.SECONDS * 12;
        String s = TimeUtils.getSmallTimeString( t );
        assertThat( s, is( "Just now" ) );
    }

    @Test
    public void TestSmallString2() {
        long t = TimeUtils.MINUTES * 12;
        String s = TimeUtils.getSmallTimeString( t );
        assertThat( s, is( "12 minutes" ) );
    }

    @Test
    public void TestSmallStringMultiple() {
        long t = TimeUtils.MINUTES * 12 + TimeUtils.DAYS * 1 + TimeUtils.HOURS * 23;
        String s = TimeUtils.getSmallTimeString( t );
        assertThat( s, is( "1 day" ) );
    }

    @Test
    public void TestSmallStringWeek() {
        long t = TimeUtils.MINUTES * 12 + TimeUtils.DAYS * 15 + TimeUtils.HOURS * 23;
        String s = TimeUtils.getSmallTimeString( t );
        assertThat( s, is( "2 weeks" ) );
    }
}
