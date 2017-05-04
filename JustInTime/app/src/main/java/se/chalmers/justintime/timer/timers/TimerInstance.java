package se.chalmers.justintime.timer.timers;

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
    private List<String> tags;
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
        tags = new ArrayList<>();
        sequentialTimers.add(timer);
        currentTimer = timer;
    }
    public TimerInstance(int id, ArrayList<Long> durations, Ticker ticker){
        this.id = id;
        sequentialTimers = new ArrayList<>();
        for (Long l: durations) {
            sequentialTimers.add(new BasicTimer(l));
        }
        currentTimer = sequentialTimers.get(0);
        this.ticker = ticker;
    }

    @Override
    public void run() {
        currentTimer.update();
        long remainingTime = currentTimer.getRemainingTime();
        if (isSendingUpdates) {
            ticker.onTick(remainingTime);
        }
        if(remainingTime < 0){
            ticker.onFinish(this);
            future.cancel(false);
        }
    }

    public void setNextTimer() {
        try {
            currentTimer = sequentialTimers.listIterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPreviousTimer() {
        try {
            currentTimer = sequentialTimers.listIterator().previous();
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

    public boolean addTag(String tag) {
        return tags.add(tag);
    }

    public List<String> getTags() {
        return tags;
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
        return future.cancel(false);
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
