package se.chalmers.justintime.database;

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
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Handles the communication with the database.
 * Created by Felix on 2017-03-30.
 * Revised by Patrik on 2017-05-01.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Database.db";

    static final String COLUMN_NAME_ID = "id";
    static final String COLUMN_NAME_TIMERID = "timer_id";
    static final String COLUMN_NAME_START_TIME = "start_time";
    static final String COLUMN_NAME_DURATION = "duration";
    static final String COLUMN_NAME_TAG = "tag";
    static final String COLUMN_NAME_LABEL = "label";

    private final String TABLE_TIMER_DATA = "timer_data";
    private final String TABLE_TIMER_TAGS = "timer_tags";
    private final String TABLE_TIMERS = "timers";

    private final String[] COLUMNS = {COLUMN_NAME_START_TIME, COLUMN_NAME_DURATION, COLUMN_NAME_TIMERID};

    private int nextAvailableId = -1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dropAllTables(db);
        createAllTables(db);
        nextAvailableId = -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables(db);
        onCreate(db);
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        dropAllTables(db);
        createAllTables(db);
        nextAvailableId = -1;
    }

    private void createAllTables(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_TIMERS +
                "(" + COLUMN_NAME_TIMERID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_LABEL + " TEXT );");
        db.execSQL(" CREATE TABLE " + TABLE_TIMER_DATA +
                "(" + COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_TIMERID + " INTEGER, " +
                COLUMN_NAME_START_TIME + " DATETIME, " +
                COLUMN_NAME_DURATION + " INTEGER );");
        db.execSQL(" CREATE TABLE " + TABLE_TIMER_TAGS +
                "(" + COLUMN_NAME_TIMERID + " INTEGER, " +
                COLUMN_NAME_TAG + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_NAME_TIMERID + ") " +
                "REFERENCES " + TABLE_TIMERS + "(" + COLUMN_NAME_TIMERID + "));");
    }

    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMER_TAGS);
    }

    public int getNextAvailableId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_TIMER_DATA, null);
        if (nextAvailableId == -1){

            nextAvailableId = res.getCount() + 1;
        }
        res.close();
        return nextAvailableId;
    }

    public int getNextAvailablePauseId(){
        int nextAvailablePauseId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_TIMER_DATA, null);
        res.moveToFirst();
        Set<Integer> noDuplicatesPauseId = new HashSet<>();
        while (!res.isAfterLast()){
            noDuplicatesPauseId.add((res.getInt(res.getColumnIndex(COLUMN_NAME_TIMERID))));
            res.moveToNext();
        }
        nextAvailablePauseId = noDuplicatesPauseId.size() + 1;
        res.close();
        return nextAvailablePauseId;
    }

    /**
     * Inserts a new timer into the database.
     * @param label The label of the new timer.
     * @param tags The tags of the new timer.
     * @return The id of the new timer.
     */
    public int insertTimer(String label, String[] tags) {
        if (label == null || tags == null) throw new NullPointerException("Label and tags are not allowed to be null.");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_LABEL, label);
        int id = (int) db.insert(TABLE_TIMERS, null, contentValues);
        for (String tag : tags) {
            contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME_TIMERID, id);
            contentValues.put(COLUMN_NAME_TAG, tag);
            db.insert(TABLE_TIMER_TAGS, null, contentValues);
        }
        return id;
    }

    public boolean insertTimerData(TimerLogEntry timerLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_ID, timerLogEntry.getDatabaseRowId());
        contentValues.put(COLUMN_NAME_TIMERID, timerLogEntry.getTimerId());
        contentValues.put(COLUMN_NAME_START_TIME, timerLogEntry.getStartTime().toEpochSecond(ZoneOffset.UTC));
        contentValues.put(COLUMN_NAME_DURATION, timerLogEntry.getDuration());
        nextAvailableId = getNextAvailableId() + 1;
        db.insert(TABLE_TIMER_DATA, null, contentValues);
        return true;
    }

    private Cursor getData(int timerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_TIMER_DATA + " where " + COLUMN_NAME_ID + " = " + timerId , null);
    }

    public TimerLogEntry getEntryById(int timeId) {
        return new TimerLogEntry(getData(timeId));
    }

    public void updateTimer(TimerLogEntry timerLogEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_START_TIME, String.valueOf(timerLogEntry.getStartTime()));
        contentValues.put(COLUMN_NAME_DURATION, String.valueOf(timerLogEntry.getDuration()));

        db.update(TABLE_TIMER_DATA, contentValues, COLUMN_NAME_ID + " = ? ", new String[]{String.valueOf(timerLogEntry.getDatabaseRowId())});
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TIMER_DATA, COLUMNS, null, null, null, null, null);
    }

    public String getTotalDuration(){
        Cursor cursor = getData();//db.rawQuery("select * from " + TABLE_TIMER_DATA, null);
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
        durationInHour = String.format(Locale.ENGLISH, "%02d:%02d:%02d",hour, minute, second).concat("  Hours");
        cursor.close();
        return durationInHour;
    }

    /**
     * Gets the label of the given timer.
     * @param timerID The id of the timer.
     * @return The label set to the timer.
     */
    public String getTimerLabel(int timerID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMERS, new String[]{COLUMN_NAME_LABEL}, COLUMN_NAME_TIMERID + " = " + timerID, null, null, null, null);
        cursor.moveToFirst();
        String label = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LABEL));
        cursor.close();
        return label;
    }

    /**
     * Gets all the tags related to the given timer.
     * @param timerID The id of the timer.
     * @return The tags of the timer as an array of Strings.
     */
    public String[] getTimerTags(int timerID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMER_TAGS, new String[]{COLUMN_NAME_TAG}, COLUMN_NAME_TIMERID + " = " + timerID, null, null, null, null);
        String[] tags = new String[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while(!cursor.isAfterLast()) {
            tags[i++] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG));
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    /**
     * Gets all information about a single timer.
     * @param timerID The id of the timer.
     * @return A TimerInfoBundle with all the information about the timer.
     */
    public TimerInfoBundle getTimerInfo(int timerID) {
        ArrayList<TimerLogEntry> times = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMER_DATA, new String[]{COLUMN_NAME_START_TIME, COLUMN_NAME_DURATION}, COLUMN_NAME_TIMERID + " = " + timerID, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            times.add(new TimerLogEntry(-1,
                    timerID,
                    LocalDateTime.ofEpochSecond(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_START_TIME)),0, ZoneOffset.UTC),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION))));

            cursor.moveToNext();
        }
        cursor.close();

        return new TimerInfoBundle(getTimerLabel(timerID), times, getTimerTags(timerID));
    }

    /**
     * Gets all the information regarding timers from the database. The information will be
     * returned as a separate TimerInfoBundle for each timer with all information about it in
     * the bundle.
     * (Could be optimized by doing a single query to the database with UNIONS and ORDER BY.)
     * @return An array with all information separated into the different timers that logged it.
     */
    public TimerInfoBundle[] getAllTimerInfo() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TIMERS, new String[]{COLUMN_NAME_TIMERID}, null, null, null, null, null);
        int[] timerIDs = new int[cursor.getCount()];
        int idColumn = cursor.getColumnIndex(COLUMN_NAME_TIMERID);

        cursor.moveToFirst();
        int i = 0;
        while(!cursor.isAfterLast()) {
            timerIDs[i++] = cursor.getInt(idColumn);
            cursor.moveToNext();
        }
        cursor.close();

        TimerInfoBundle[] timerInfoBundles = new TimerInfoBundle[cursor.getCount()];
        for (i = 0; i < timerIDs.length; i++) {
            timerInfoBundles[i] = getTimerInfo(timerIDs[i]);
        }

        return timerInfoBundles;
        /*
        ArrayList<TimerInfoBundle> timerInfoBundles = new ArrayList<>();
        SparseArray<ArrayList<TimerLogEntry>> timers = new SparseArray<>();
        ArrayList<TimerLogEntry> timer;
        Cursor cursor = getData();
        cursor.moveToFirst();
        int id;
        while(!cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_TIMERID));
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
        cursor.close();
        for (int i=0; i<timers.size(); i++) {
            timer = timers.valueAt(i);
            timerInfoBundles.add(new TimerInfoBundle("Undefined",
                    timer.toArray(new TimerLogEntry[timer.size()]),
                    new String[] {"Undefined"}));
        }
        return timerInfoBundles.toArray(new TimerInfoBundle[timerInfoBundles.size()]);
        */
    }
}