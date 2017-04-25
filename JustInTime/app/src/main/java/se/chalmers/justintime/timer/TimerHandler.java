package se.chalmers.justintime.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
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

    public void startTimer(int timerInstance) {
        scheduleTimerWithFixedRate(findById(timerInstance), 20);
    }

    private void scheduleTimerWithFixedRate(TimerInstance timer, long fixedRateInMilliSeconds) {
        ScheduledFuture sf = timerSchedulerExecutor.scheduleAtFixedRate(timer, 0, fixedRateInMilliSeconds, TimeUnit.MILLISECONDS);
        timer.setFuture(sf);
    }

    public boolean removeTimer(int timerId) {
        return stopTimer(timerId) && timers.remove(findById(timerId));
    }

    public boolean stopTimer(int timerId) {
        TimerInstance t = findById(timerId);
        return t.stop();
    }

    private TimerInstance findById(int id){
        for (TimerInstance t: timers) {
            if(t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public long resetTimer(int timerId) {
        return findById(timerId).reset();
    }
}
