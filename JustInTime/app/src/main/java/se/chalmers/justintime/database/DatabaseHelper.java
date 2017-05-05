package se.chalmers.justintime.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Handles the communication with the database.
 * Created by Felix on 2017-03-30.
 * Revised by Patrik on 2017-05-01.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Database.db";

    static final String COLUMN_NAME_TIMERID = "timer_id";
    static final String COLUMN_NAME_START_TIME = "start_time";
    static final String COLUMN_NAME_DURATION = "duration";
    static final String COLUMN_NAME_TAG = "tag";
    static final String COLUMN_NAME_LABEL = "label";

    private final String TABLE_TIMER_DATA = "timer_data";
    private final String TABLE_TIMER_TAGS = "timer_tags";
    private final String TABLE_TIMERS = "timers";

    private final String[] COLUMNS = {COLUMN_NAME_START_TIME, COLUMN_NAME_DURATION, COLUMN_NAME_TIMERID};


    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dropAllTables(db);
        createAllTables(db);
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
    }

    private void createAllTables(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_TIMERS +
                "(" + COLUMN_NAME_TIMERID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_LABEL + " TEXT );");
        db.execSQL(" CREATE TABLE " + TABLE_TIMER_DATA +
                "(" + COLUMN_NAME_TIMERID + " INTEGER, " +
                COLUMN_NAME_START_TIME + " DATETIME, " +
                COLUMN_NAME_DURATION + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_NAME_TIMERID + ") " +
                "REFERENCES " + TABLE_TIMERS + "(" + COLUMN_NAME_TIMERID + "));");
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

    /**
     * Inserts a new timer into the database.
     *
     * @param label The label of the new timer.
     * @param tags  The tags of the new timer.
     * @return The id of the new timer.
     */
    public int insertTimer(String label, String[] tags) {
        if (label == null || label.isEmpty() || tags == null || tags.length == 0) {
            throw new IllegalArgumentException("Label and tags are not allowed to be null or empty.");
        }

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
        contentValues.put(COLUMN_NAME_TIMERID, timerLogEntry.getTimerId());
        contentValues.put(COLUMN_NAME_START_TIME, timerLogEntry.getStartTime().toEpochSecond(ZoneOffset.UTC));
        contentValues.put(COLUMN_NAME_DURATION, timerLogEntry.getDuration());
        db.insert(TABLE_TIMER_DATA, null, contentValues);
        return true;
    }

    /**
     * Sets the given tags to the given timer. Any old tags will be removed and replaced by
     * the new ones.
     *
     * @param timerId The id of the timer to set the tags to.
     * @param tags    The tags to set to the timer.
     */
    public void setTimerTags(int timerId, String[] tags) {
        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags are not allowed to be null or empty.");

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMER_TAGS, COLUMN_NAME_TIMERID + " = " + timerId, null);
        ContentValues contentValues = new ContentValues();
        for (String tag : tags) {
            contentValues.put(COLUMN_NAME_TIMERID, timerId);
            contentValues.put(COLUMN_NAME_TAG, tag);
            db.insert(TABLE_TIMER_TAGS, null, contentValues);
        }
    }

    /**
     * Changes the label of the given timer.
     *
     * @param timerId  The id of the timer.
     * @param newLabel The new label for the timer.
     */
    public void updateTimerLabel(int timerId, String newLabel) {
        if (newLabel == null || newLabel.isEmpty())
            throw new IllegalArgumentException("Label is not allowed to be null or empty.");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_LABEL, newLabel);
        int changed = db.update(TABLE_TIMERS, contentValues, COLUMN_NAME_TIMERID + " = " + timerId, null);
        if (changed < 1) {
            throw new IllegalArgumentException("No timer with id '" + timerId + "' was found.");
        } else if (changed > 1) {
            throw new SQLiteConstraintException("Too many rows were affected by the update (" + changed + ")");
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TIMER_DATA, COLUMNS, null, null, null, null, null);
    }

    public String getTotalDuration() {
        Cursor cursor = getData();//db.rawQuery("select * from " + TABLE_TIMER_DATA, null);
        cursor.moveToFirst();
        int duration = 0;
        while (!cursor.isAfterLast()) {
            duration += cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION));
            cursor.moveToNext();
        }
        String durationInHour;
        long hour = TimeUnit.MILLISECONDS.toHours(duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long second = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        durationInHour = String.format(Locale.ENGLISH, "%02d:%02d:%02d", hour, minute, second).concat("  Hours");
        cursor.close();
        return durationInHour;
    }

    /**
     * Gets the label of the given timer.
     *
     * @param timerId The id of the timer.
     * @return The label set to the timer.
     */
    public String getTimerLabel(int timerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMERS, new String[]{COLUMN_NAME_LABEL}, COLUMN_NAME_TIMERID + " = " + timerId, null, null, null, null);
        cursor.moveToFirst();
        String label = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LABEL));
        cursor.close();
        return label;
    }

    /**
     * Gets all the tags related to the given timer.
     *
     * @param timerId The id of the timer.
     * @return The tags of the timer as an array of Strings.
     */
    public String[] getTimerTags(int timerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMER_TAGS, new String[]{COLUMN_NAME_TAG}, COLUMN_NAME_TIMERID + " = " + timerId, null, null, null, null);
        String[] tags = new String[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            tags[i++] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG));
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    /**
     * Gets all information about a single timer.
     *
     * @param timerId The id of the timer.
     * @return A TimerInfoBundle with all the information about the timer.
     */
    public TimerInfoBundle getTimerInfo(int timerId) {
        ArrayList<TimerLogEntry> times = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMER_DATA, new String[]{COLUMN_NAME_START_TIME, COLUMN_NAME_DURATION}, COLUMN_NAME_TIMERID + " = " + timerId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            times.add(new TimerLogEntry(timerId,
                    LocalDateTime.ofEpochSecond(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_START_TIME)), 0, ZoneOffset.UTC),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DURATION))));

            cursor.moveToNext();
        }
        cursor.close();

        return new TimerInfoBundle(timerId, getTimerLabel(timerId), times, getTimerTags(timerId));
    }

    /**
     * Gets all the information regarding timers from the database. The information will be
     * returned as a separate TimerInfoBundle for each timer with all information about it in
     * the bundle.
     * (Could be optimized by doing a single query to the database with UNIONS and ORDER BY.)
     *
     * @return An array with all information separated into the different timers that logged it.
     */
    public TimerInfoBundle[] getAllTimerInfo() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TIMERS, new String[]{COLUMN_NAME_TIMERID}, null, null, null, null, null);
        int[] timerIds = new int[cursor.getCount()];
        int idColumn = cursor.getColumnIndex(COLUMN_NAME_TIMERID);

        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            timerIds[i++] = cursor.getInt(idColumn);
            cursor.moveToNext();
        }
        cursor.close();

        TimerInfoBundle[] timerInfoBundles = new TimerInfoBundle[cursor.getCount()];
        for (i = 0; i < timerIds.length; i++) {
            timerInfoBundles[i] = getTimerInfo(timerIds[i]);
        }

        return timerInfoBundles;
    }

    public String[] getTags() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIMER_TAGS, new String[]{COLUMN_NAME_TAG}, null, null, COLUMN_NAME_TAG, null, null);
        String[] tags = new String[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            tags[i++] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TAG));
            cursor.moveToNext();
        }
        cursor.close();
        return tags;
    }

    public long getTagTime(String tag) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select duration from timer_data where timer_id in (select timer_id from timer_tags where tag = ?)", new String[]{tag});
        //Cursor cursor = db.query(TABLE_TIMER_TAGS, new String[]{COLUMN_NAME_TIMERID}, tag + " = " + COLUMN_NAME_TAG, null, COLUMN_NAME_TIMERID, null, null);
        cursor.moveToFirst();
        int allTimePerTag = 0;
        while (!cursor.isAfterLast()) {
            allTimePerTag += cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_DURATION));
            cursor.moveToNext();
        }
        cursor.close();
        return allTimePerTag;
    }
}