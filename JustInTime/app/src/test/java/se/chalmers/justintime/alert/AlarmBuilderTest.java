package se.chalmers.justintime.alert;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by David on 2017-04-01.
 */


@RunWith(MockitoJUnitRunner.class)
public class AlarmBuilderTest {
    private AlarmBuilder alarmBuilder;
    @Mock
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        alarmBuilder = new AlarmBuilder(mockContext);
    }

    @Test
    public void getAlarmInstance() throws Exception {
        Alarm alarm = alarmBuilder.getAlarmInstance();
        assertThat(alarm,instanceOf(Alarm.class));
        assertThat(alarm,instanceOf(BasicAlarm.class));
    }

    @Test
    public void getSoundAlarmInstance() throws Exception {
        alarmBuilder.setUseSound(true);
        Alarm alarm = alarmBuilder.getAlarmInstance();
        assertThat(alarm,instanceOf(Alarm.class));
        assertThat(alarm,instanceOf(SoundAlarm.class));
    }

    @Test
    public void getVibrationAlarmInstance() throws Exception {
        alarmBuilder.setUseVibration(true);
        Alarm alarm = alarmBuilder.getAlarmInstance();
        assertThat(alarm,instanceOf(Alarm.class));
        assertThat(alarm,instanceOf(VibrationAlarm.class));
    }
}