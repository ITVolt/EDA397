package se.chalmers.justintime;

import se.chalmers.justintime.database.TimerInfoBundle;
import se.chalmers.justintime.database.TimerLogEntry;

/**
 * Holds information about a statistical bundle.
 * Created by Patrik on 2017-04-20.
 */

public class StatisticsBundle {

    private final String label;
    private final TimerInfoBundle[] timers;
    private final long totalDuration;

    public StatisticsBundle(String label, TimerInfoBundle[] timerInfoBundles) {
        this.label = label;
        this.timers = timerInfoBundles;
        long duration = 0;
        for (TimerInfoBundle tib : timers) {
            duration += tib.getTotalDuration();
        }
        this.totalDuration = duration;
    }

    public String getLabel() {
        return label;
    }

    public TimerInfoBundle[] getTimers() {
        return timers;
    }

    public long getTotalDuration() {
        return totalDuration;
    }
}
