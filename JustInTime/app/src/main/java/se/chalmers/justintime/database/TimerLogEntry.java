package se.chalmers.justintime.database;

import android.database.Cursor;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

/**
 * Used to create and read and write timer data from and to the database.
 * Created by David on 2017-04-04.
 */

public class TimerLogEntry {
    private  int timerId;
    private LocalDateTime startTime;
    private long duration;

    public TimerLogEntry(int timerId, LocalDateTime startTime, long duration) {
        this.timerId = timerId;
        this.startTime = startTime;
        this.duration = duration;
    }

    public TimerLogEntry(Cursor cursor) {
        cursor.moveToFirst();
        this.timerId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_TIMERID));
        this.startTime = LocalDateTime.ofEpochSecond(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_START_TIME)),0, ZoneOffset.UTC);
        this.duration = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_DURATION));
    }

    public int getTimerId() {
        return timerId;
    }

    public void setTimerId(int timerId) {
        this.timerId = timerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


}
