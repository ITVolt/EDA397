package se.chalmers.justintime.timer.timers;

import android.util.Log;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import se.chalmers.justintime.timer.Ticker;

/**
 * Created by David on 2017-04-20.
 */

public class TimerInstance implements Runnable{
    private int id;
    private final Ticker ticker;
    private List<AbstractTimer> sequentialTimers;
    private AbstractTimer currentTimer;
    private ScheduledFuture future;
    private LocalDateTime startTime;

    public void setSendingUpdates(boolean sendingUpdates) {
        isSendingUpdates = sendingUpdates;
        ticker.onTick(currentTimer.getRemainingTime()); //Make a tick with the current time to inform the fragment about the current state
    }

    private boolean isSendingUpdates = false;

    public TimerInstance(int id, AbstractTimer timer, Ticker ticker) {
        this.id = id;
        this.ticker = ticker;
        this.sequentialTimers = new ArrayList<>();
        sequentialTimers.add(timer);
        currentTimer = timer;
    }
    public TimerInstance(int id, List<Long> durations, Ticker ticker){
        this.id = id;
        sequentialTimers = new ArrayList<>();
        for (Long l: durations) {
            sequentialTimers.add(new BasicTimer(l));
        }
        currentTimer = sequentialTimers.get(0);
        this.ticker = ticker;
        Log.d("TimerInstance", "new TimerInstance with " + sequentialTimers.size() + " timers");
    }

    @Override
    public void run() {
        currentTimer.update();
        long remainingTime = currentTimer.getRemainingTime();
        if (isSendingUpdates) {
            ticker.onTick(remainingTime);
            //Log.d("TimerInstance", "Tick");
        }
        if(remainingTime < 0){
            ticker.onFinish(this);
            if(setNextTimer()){
                Log.d("TimerInstance", "Starting next timer");
            }else{
                future.cancel(false);
            }
        }
    }

    public boolean setNextTimer() {
        try {
            currentTimer = sequentialTimers.get(sequentialTimers.indexOf(currentTimer) + 1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPreviousTimer() {
        try {
            currentTimer = sequentialTimers.get(sequentialTimers.indexOf(currentTimer) - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTimerDone() {
        return currentTimer.isDone();
    }

    public long getRemainingTime() {
        return currentTimer.getRemainingTime();
    }

    public int getId() {
        return id;
    }

    public long reset() {
        currentTimer.stop();
        currentTimer = sequentialTimers.get(0);
        currentTimer.stop();
        return currentTimer.getRemainingTime();
    }

    public boolean stop(){
        currentTimer.pause();
        return future != null && future.cancel(false);
    }
    public void start(ScheduledFuture future) {
        this.future = future;
        startTime = LocalDateTime.now();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
    }
}
