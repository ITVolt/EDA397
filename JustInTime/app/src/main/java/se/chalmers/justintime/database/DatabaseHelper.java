package se.chalmers.justintime.database;

/**
 * Created by Felix on 2017-03-30.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.util.ArrayList;
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        if (nextAvailableId == -1){

            nextAvailableId = res.getCount() + 1;
        }
        res.close();
        return nextAvailableId;
    }

    public int getNextAvailablePauseId(){
        int nextAvailablePauseId;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
            res.moveToFirst();
        Set<Integer> noDuplicatesPauseId = new HashSet<>();
            while (!res.isAfterLast()){
                noDuplicatesPauseId.add((res.getInt(res.getColumnIndex(COLUMN_NAME_GROUPID))));
                res.moveToNext();
            }
            nextAvailablePauseId = noDuplicatesPauseId.size() + 1;
        res.close();
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
        return db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = " + timerId , null);
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

    public TimerInfoBundle[] getAllTimerData() {
        ArrayList<TimerInfoBundle> timerInfoBundles = new ArrayList<>();

        SparseArray<ArrayList<TimerLogEntry>> timers = new SparseArray<>();
        ArrayList<TimerLogEntry> timer;
        Cursor cursor = getData();
        cursor.moveToFirst();

        int id;
        while(!cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_GROUPID));
            timer = timers.get(id);
            if (timer == null) {
                timers.append(id, new ArrayList<TimerLogEntry>());
                timer = timers.get(id);
            }
            timer.add(new TimerLogEntry(-1,
                    id,
                    LocalDateTime.ofEpochSecond(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_START_TIME)),0, ZoneOffset.UTC),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION))));

            cursor.moveToNext();
        }

        for (int i=0; i<timers.size(); i++) {
            timer = timers.valueAt(i);
            timerInfoBundles.add(new TimerInfoBundle("Undefined",
                    timer.toArray(new TimerLogEntry[timer.size()]),
                    new String[] {"Undefined"}));
        }
        return timerInfoBundles.toArray(new TimerInfoBundle[timerInfoBundles.size()]);
    }
}