package se.chalmers.justintime.timer;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import se.chalmers.justintime.timer.timers.BasicTimer;
import se.chalmers.justintime.timer.timers.TimerInstance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by David on 2017-04-16.
 */
@RunWith(AndroidJUnit4.class)
public class TimerExecutorTest {
    private TimerHandler timerHandler;
    private TimerInstance basicTimer,basicTimer1, timerThatWillNotFinish;
    @Before
    public void setUp() throws Exception {
        Ticker ticker = new Ticker() {
            @Override
            public void onTick(long time) {

            }
        };
        timerHandler = new TimerHandler();
        basicTimer = new TimerInstance(new BasicTimer(200),ticker);
        basicTimer1 = new TimerInstance(new BasicTimer(200),ticker);
        timerThatWillNotFinish = new TimerInstance(new BasicTimer(2000L),ticker);
    }

    @Test
    public void addAndStartTimers() throws Exception {
        assertFalse(basicTimer.isTimerDone());
        assertFalse(basicTimer1.isTimerDone());
        assertFalse(timerThatWillNotFinish.isTimerDone());
        timerHandler.addTimer(basicTimer);
        timerHandler.addTimer(basicTimer1);
        timerHandler.addTimer(timerThatWillNotFinish);
        timerHandler.startTimer(basicTimer);
        timerHandler.startTimer(basicTimer1);
        timerHandler.startTimer(timerThatWillNotFinish);
        Thread.sleep(300);
        assertTrue(basicTimer.isTimerDone());
        assertTrue(basicTimer1.isTimerDone());
        assertFalse(timerThatWillNotFinish.isTimerDone());
    }

}