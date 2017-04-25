package se.chalmers.justintime.timer.timers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import se.chalmers.justintime.timer.ParcelableTimer;
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

    public TimerInstance(int id, AbstractTimer timer, Ticker ticker) {
        this.id = id;
        this.ticker = ticker;
        this.sequentialTimers = new ArrayList<>();
        tags = new ArrayList<>();
        sequentialTimers.add(timer);
        currentTimer = timer;
    }
    public TimerInstance(ParcelableTimer parcelableTimer, Ticker ticker){
        id = parcelableTimer.getId();
        sequentialTimers = new ArrayList<>();
        for (Long l: parcelableTimer.getDurations()) {
            sequentialTimers.add(new BasicTimer(l));
        }
        currentTimer = sequentialTimers.get(0);
        this.ticker = ticker;
    }

    @Override
    public void run() {
        currentTimer.update();
        ticker.onTick(currentTimer.getRemainingTime());
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
    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }
}
