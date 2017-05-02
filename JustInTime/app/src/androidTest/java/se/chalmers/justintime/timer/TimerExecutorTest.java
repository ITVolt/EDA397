package se.chalmers.justintime.timer;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import se.chalmers.justintime.timer.timers.BasicTimer;
import se.chalmers.justintime.timer.timers.TimerInstance;

import static org.junit.Assert.assertEquals;
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

            @Override
            public void onFinish() {

            }
        };
        timerHandler = new TimerHandler();
        basicTimer = new TimerInstance(1, new BasicTimer(200),ticker);
        basicTimer1 = new TimerInstance(2, new BasicTimer(200),ticker);
        timerThatWillNotFinish = new TimerInstance(3, new BasicTimer(2000L),ticker);
    }

    @Test
    public void addAndStartTimers() throws Exception {
        assertFalse(basicTimer.isTimerDone());
        assertFalse(basicTimer1.isTimerDone());
        assertFalse(timerThatWillNotFinish.isTimerDone());
        timerHandler.addTimer(basicTimer);
        timerHandler.addTimer(basicTimer1);
        timerHandler.addTimer(timerThatWillNotFinish);
        timerHandler.startTimer(1);
        timerHandler.startTimer(2);
        timerHandler.startTimer(3);
        Thread.sleep(300);
        assertTrue(basicTimer.isTimerDone());
        assertTrue(basicTimer1.isTimerDone());
        assertFalse(timerThatWillNotFinish.isTimerDone());

    }

    @Test
    public void stopTimer() throws InterruptedException {
        timerHandler.addTimer(timerThatWillNotFinish);
        timerHandler.startTimer(3);
        Thread.sleep(300);
        timerHandler.stopTimer(3);
        long l = timerThatWillNotFinish.getRemainingTime();
        Thread.sleep(300);
        long l2 = timerThatWillNotFinish.getRemainingTime();
        assertTrue(l == timerThatWillNotFinish.getRemainingTime());
    }


    @Test
    public void resumeTimer() throws InterruptedException {
        timerHandler.addTimer(timerThatWillNotFinish);
        timerHandler.startTimer(3);
        Thread.sleep(100);
        timerHandler.stopTimer(3);
        long pause = timerThatWillNotFinish.getRemainingTime();
        timerHandler.startTimer(3);
        Thread.sleep(100);
        long after100 = timerThatWillNotFinish.getRemainingTime();
        assertTrue(pause > after100);
    }

    @Test
    public void resetTimer() throws InterruptedException {
        timerHandler.addTimer(timerThatWillNotFinish);
        timerHandler.startTimer(3);
        Thread.sleep(1000);
        assertFalse(2000L == timerThatWillNotFinish.getRemainingTime());
        assertEquals(2000L, timerHandler.resetTimer(3));
        Thread.sleep(100);
        long l = timerThatWillNotFinish.getRemainingTime();
        System.out.println(l);
    }


}