package se.chalmers.justintime.database;

import android.database.Cursor;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

/**
 * Created by David on 2017-04-04.
 */

public class TimerLogEntry {
    int id;
    int groupId;
    LocalDateTime startTime;
    long duration;

    public  static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_GROUPID = "group_id";
    public static final String COLUMN_NAME_START_TIME = "start_time";
    public static final String COLUMN_NAME_DURATION = "duration";

    public static final String TABLE_NAME = "timer_data";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String SQL_CREATE_ENTRIES = " CREATE TABLE " + TABLE_NAME +
            "(" + COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_NAME_GROUPID + " INTEGER, " +
            COLUMN_NAME_START_TIME + " DATETIME, " +
            COLUMN_NAME_DURATION + " INTEGER );";

    public static final String[] COLUMNS = {COLUMN_NAME_START_TIME, COLUMN_NAME_DURATION, COLUMN_NAME_GROUPID};

    public TimerLogEntry(int id, int groupId, LocalDateTime startTime, long duration) {
        this.id = id;
        this.groupId = groupId;
        this.startTime = startTime;
        this.duration = duration;
    }

    public TimerLogEntry(Cursor cursor) {
        cursor.moveToFirst();
        this.id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
        this.groupId = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_GROUPID));
        this.startTime = LocalDateTime.ofEpochSecond(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_START_TIME)),0, ZoneOffset.UTC);
        this.duration = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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
