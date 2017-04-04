package se.chalmers.justintime;

/**
 * Created by Felix on 2017-03-30.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Database.db";

    private static final String TABLE_NAME = "timer_data";

    private static final String COLUMN_NAME_TIMERID = "timer_id";
    private static final String COLUMN_NAME_GROUPID = "group_id";
    private static final String COLUMN_NAME_START_TIME = "start_time";
    private static final String COLUMN_NAME_DURATION = "duration";

    private static final String SQL_CREATE_ENTRIES = " CREATE TABLE " + TABLE_NAME +
            "(" + COLUMN_NAME_TIMERID + " INTEGER PRIMARY KEY, " +
            COLUMN_NAME_GROUPID + " INTEGER, " +
            COLUMN_NAME_START_TIME + " INTEGER, " +
            COLUMN_NAME_DURATION + " INTEGER );";


    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean insertTimer(String timerId, String groupId, String startTime, String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_TIMERID, timerId);
        contentValues.put(COLUMN_NAME_GROUPID, groupId);
        contentValues.put(COLUMN_NAME_START_TIME, startTime);
        contentValues.put(COLUMN_NAME_DURATION, duration);

        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(String timerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_TIMERID + " =" + timerId, null);
        return res;
    }

    /**
     * Method for updating the status in an error report with a specific error id
     *
     * @param timerId unique ID for the error report which to update
     */
    public void updateTimer(String timerId, String startTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_START_TIME, startTime);

        db.update(TABLE_NAME, contentValues, COLUMN_NAME_TIMERID + " = ? ", new String[]{timerId});
    }

}