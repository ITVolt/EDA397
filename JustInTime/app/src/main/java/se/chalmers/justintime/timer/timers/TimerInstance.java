package se.chalmers.justintime.timer.timers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 2017-04-20.
 */

public class TimerInstance {
    private List<AbstractTimer> sequentialTimers;
    private AbstractTimer currentTimer;
    private List<String> tags;

    public TimerInstance(AbstractTimer timer) {
        this.sequentialTimers = new ArrayList<>();
        tags = new ArrayList<>();
        sequentialTimers.add(timer);
        currentTimer = timer;
    }

    public AbstractTimer getCurrentTimer() {
        return currentTimer;
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

    public boolean addTag(String tag) {
        return tags.add(tag);
    }

    public List<String> getTags() {
        return tags;
    }
}
