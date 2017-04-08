package se.chalmers.justintime.database;

/**
 * Created by Felix on 2017-03-30.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.threeten.bp.ZoneOffset;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static se.chalmers.justintime.database.TimerLogEntry.COLUMNS;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_DURATION;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_GROUPID;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_ID;
import static se.chalmers.justintime.database.TimerLogEntry.COLUMN_NAME_START_TIME;
import static se.chalmers.justintime.database.TimerLogEntry.SQL_DELETE_ENTRIES;
import static se.chalmers.justintime.database.TimerLogEntry.TABLE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Database.db";
    private int nextAvailableId = -1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(TimerLogEntry.SQL_CREATE_ENTRIES);
        nextAvailableId = -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(TimerLogEntry.SQL_CREATE_ENTRIES);
        nextAvailableId = -1;
    }

    public int getNextAvailableId(){
        if (nextAvailableId == -1){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
            nextAvailableId = res.getCount() + 1;
        }
        return nextAvailableId;
    }

    public int getNextAvailablePauseId(){
        int nextAvailablePauseId;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
            res.moveToFirst();
        Set noDuplicatesPauseId = new HashSet();
            while (!res.isAfterLast()){
                noDuplicatesPauseId.add(res.getInt(res.getColumnIndex(COLUMN_NAME_GROUPID)));
                res.moveToNext();
            }
            nextAvailablePauseId = noDuplicatesPauseId.size() + 1;

        return nextAvailablePauseId;
    }


    public boolean insertTimer(TimerLogEntry timerLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_ID, timerLogEntry.getId());
        contentValues.put(COLUMN_NAME_GROUPID, timerLogEntry.getGroupId());
        contentValues.put(COLUMN_NAME_START_TIME, timerLogEntry.getStartTime().toEpochSecond(ZoneOffset.UTC));
        contentValues.put(COLUMN_NAME_DURATION, timerLogEntry.getDuration());
        nextAvailableId = getNextAvailableId() + 1;
        db.insert(TABLE_NAME, null, contentValues);
        return true;
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

    public String getTotalDuration(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = getData();//db.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToFirst();
        int duration = 0;
        while(!cursor.isAfterLast()) {
            duration += cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION));
            cursor.moveToNext();
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