package se.chalmers.justintime.timer.timers;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.justintime.timer.Ticker;

/**
 * Created by David on 2017-04-20.
 */

public class TimerInstance implements Runnable{
    private final Ticker ticker;
    private List<AbstractTimer> sequentialTimers;
    private AbstractTimer currentTimer;
    private List<String> tags;

    public TimerInstance(AbstractTimer timer,Ticker ticker) {
        this.ticker = ticker;
        this.sequentialTimers = new ArrayList<>();
        tags = new ArrayList<>();
        sequentialTimers.add(timer);
        currentTimer = timer;
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

}
