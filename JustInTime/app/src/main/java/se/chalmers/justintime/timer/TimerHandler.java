package se.chalmers.justintime.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.chalmers.justintime.timer.timers.TimerInstance;

/**
 * Created by David on 2017-04-20.
 */

public class TimerHandler {
    private List<TimerInstance> timers;
    private ScheduledThreadPoolExecutor timerSchedulerExecutor;

    public TimerHandler() {
        this.timers = new ArrayList<>();
//      The parameter indicates the maximum allowed number of threads/timers
        timerSchedulerExecutor = new ScheduledThreadPoolExecutor(100);
    }

    public void addTimer(TimerInstance timerInstance) {
        timers.add(timerInstance);
    }

    public void startTimer(TimerInstance timerInstance) {
        scheduleTimerWithFixedRate(timerInstance, 20);
    }

    private void scheduleTimerWithFixedRate(TimerInstance timer, long fixedRateInMilliSeconds) {
        timerSchedulerExecutor.scheduleAtFixedRate(timer, 0, fixedRateInMilliSeconds, TimeUnit.MILLISECONDS);
    }

    public boolean removeTimer(TimerInstance timerInstance) {
        return stopTimer(timerInstance) && timers.remove(timerInstance);
    }

    public boolean stopTimer(TimerInstance timer) {
        return timerSchedulerExecutor.remove(timer);
    }
}
