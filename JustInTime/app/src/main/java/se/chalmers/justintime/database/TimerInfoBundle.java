package se.chalmers.justintime.database;

import android.util.Pair;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a the logs of a timer.
 * Created by Patrik on 2017-04-20.
 */

public class TimerInfoBundle {

    private final String label;
    private final int id;
    private final String[] tags;
    private final ArrayList<Pair<LocalDateTime, Long>> times;
    private final long totalDuration;

    public TimerInfoBundle(int id, String label, List<TimerLogEntry> times, String[] tags) {
        this.label = label;
        this.id = id;
        this.tags = tags;
        this.times = new ArrayList<>();
        long totDur = 0;
        for (TimerLogEntry tle : times) {
            this.times.add(new Pair<>(tle.getStartTime(), tle.getDuration()));
            totDur += tle.getDuration();
        }
        this.totalDuration = totDur;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    public String[] getTags() {
        return tags;
    }

    public ArrayList<Pair<LocalDateTime, Long>> getTimes() {
        return times;
    }

    public long getTotalDuration() {
        return totalDuration;
    }
}
