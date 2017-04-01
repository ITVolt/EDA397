package se.chalmers.justintime.alert;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by David on 2017-04-01.
 */
public class AlarmBuilderTest {
    private AlarmBuilder alarmBuilder;

    @Before
    public void setUp() throws Exception {
        alarmBuilder = new AlarmBuilder(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void getAlarmInstance() throws Exception {
        alarmBuilder.setUseSound(true);
        alarmBuilder.setUseVibration(true);
        Alarm alarm = alarmBuilder.getAlarmInstance();
        alarm.alert();
    }

}