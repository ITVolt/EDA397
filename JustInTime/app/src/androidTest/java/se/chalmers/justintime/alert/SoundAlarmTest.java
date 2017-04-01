package se.chalmers.justintime.alert;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by David on 2017-04-01.
 */
public class SoundAlarmTest {
    private SoundAlarm soundAlarm;

    @Before
    public void setUp() throws Exception {
        soundAlarm = new SoundAlarm(new BasicAlarm(), InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void alert() throws Exception {
        soundAlarm.alert();
    }

    @Test
    public void dismissAlarm() throws Exception {
        soundAlarm.alert();
        soundAlarm.dismissAlarm();
    }

}