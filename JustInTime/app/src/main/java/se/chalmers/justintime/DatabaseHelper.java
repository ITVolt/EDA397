package se.chalmers.justintime;

/**
 * Created by Felix on 2017-03-30.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoField;

import java.util.concurrent.TimeUnit;

import se.chalmers.justintime.database.TimerLogEntry;

import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_DURATION;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_GROUPID;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_ID;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_START_TIME;
import static se.chalmers.justintime.database.TimerLogEntry.SQL_DELETE_ENTRIES;
import static se.chalmers.justintime.database.TimerLogEntry.TABLE_NAME;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMNS_SUMMARY;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMNS;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMNS_DURATIONS;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Database.db";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(TimerLogEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean insertTimer(TimerLogEntry timerLogEntry) {
        long start = timerLogEntry.getStartTime().toEpochSecond(ZoneOffset.UTC);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_ID, timerLogEntry.getId());
        contentValues.put(COLUMN_NAME_GROUPID, timerLogEntry.getGroupId());
        contentValues.put(COLUMN_NAME_START_TIME, 1);// timerLogEntry.getStartTime().toEpochSecond(ZoneOffset.UTC)); FIXME This is not the correct time.
        contentValues.put(COLUMN_NAME_DURATION, timerLogEntry.getDuration());
        long id = db.insert(TABLE_NAME, null, contentValues);
        return id > -1  ;
    }

    private Cursor getData(int timerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = " + timerId , null);
        return res;
    }

    public TimerLogEntry getEntryById(int timeId) {
        return new TimerLogEntry(getData(timeId));
    }


    public void updateTimer(TimerLogEntry timerLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_START_TIME, String.valueOf(timerLogEntry.getStartTime()));
        contentValues.put(COLUMN_NAME_DURATION, String.valueOf(timerLogEntry.getDuration()));

        db.update(TABLE_NAME, contentValues, COLUMN_NAME_ID + " = ? ", new String[]{String.valueOf(timerLogEntry.getId())});
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
    }

    public Cursor getSummary(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, COLUMNS_SUMMARY, null, null, COLUMN_NAME_GROUPID, null, null);
    }
    public String getTotalDuration(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, COLUMNS_DURATIONS, null, null, null, null, null);
        int duration = 0;
        for(cursor.moveToFirst();cursor.isLast();cursor.moveToNext())
        {
            duration += cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION));
        }

        String durationInHour;
        long hour = TimeUnit.MILLISECONDS.toHours(duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long second = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        durationInHour = String.format("%02d:%02d:%02d",hour, minute, second).concat("  Hours");
        cursor.close();
        return durationInHour;
    }

}