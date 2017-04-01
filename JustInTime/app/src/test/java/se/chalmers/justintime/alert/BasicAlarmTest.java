package se.chalmers.justintime.alert;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by David on 2017-04-01.
 */
public class BasicAlarmTest {
    private BasicAlarm basicAlarm;

    @Before
    public void setUp() {
        basicAlarm = new BasicAlarm();
    }

    @Test
    public void alert() throws Exception {
        basicAlarm.alert();
        assertTrue(basicAlarm.isAlarmBroadcasted());
    }

    @Test
    public void dismissAlarm() throws Exception {
        basicAlarm.dismissAlarm();
        assertFalse(basicAlarm.isAlarmBroadcasted());
    }

    @Test
    public void isAlarmBroadcasted() throws Exception {
        boolean status = basicAlarm.isAlarmBroadcasted();
        assertEquals(status, basicAlarm.isAlarmBroadcasted());
        basicAlarm.setAlarmBroadcast(false);
        assertFalse(basicAlarm.isAlarmBroadcasted());
        basicAlarm.setAlarmBroadcast(true);
        assertTrue(basicAlarm.isAlarmBroadcasted());
    }

}