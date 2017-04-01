package se.chalmers.justintime.alert;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by David on 2017-04-01.
 */
@RunWith(AndroidJUnit4.class)
public class VibrationAlarmTest {
    private VibrationAlarm vibrationAlarm;

    @Before
    public void setUp() throws Exception {
        vibrationAlarm = new VibrationAlarm(new BasicAlarm(),InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testAlarm() throws Exception {
        vibrationAlarm.alert();
    }

    @Test
    public void testDismiss() throws Exception {
        vibrationAlarm.alert();
        vibrationAlarm.dismissAlarm();
    }

}