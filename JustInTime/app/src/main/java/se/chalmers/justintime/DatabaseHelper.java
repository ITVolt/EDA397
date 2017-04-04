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

import se.chalmers.justintime.database.TimerLogEntry;

import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_DURATION;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_GROUPID;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_ID;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_START_TIME;
import static se.chalmers.justintime.database.TimerLogEntry.SQL_DELETE_ENTRIES;
import static se.chalmers.justintime.database.TimerLogEntry.TABLE_NAME;


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

}