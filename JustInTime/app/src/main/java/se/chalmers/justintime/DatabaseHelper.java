package se.chalmers.justintime;

/**
 * Created by Felix on 2017-03-30.
 */

//import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{

    //Unchangeable strings which is used when the database is created
    private static final String TABLE_NAME = "TimerData";
    private static final String COLUMN_NAME_ENTRYID = "TimerId";
    private static final String COLUMN_NAME_START_TIME = "StartTime";


    //Helpstrings for queries
    private static final String TEXT_TYPE = " TEXT ";
    private static final String COMMA_SEP = ",";
    private static final String NUMBER_TYPE = " INTEGER";

    //String to create table in DB
    private static final String SQL_CREATE_ENTRIES = " CREATE TABLE " + TABLE_NAME +
            "(" + COLUMN_NAME_ENTRYID + " TEXT PRIMARY KEY, " +
            COLUMN_NAME_START_TIME + TEXT_TYPE + ")"  ;

    //String to drop database
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //Strings with database info
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Database.db";

    //constructor
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean insertTimer(String timerId, String startTime){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_ENTRYID, timerId);
        contentValues.put(COLUMN_NAME_START_TIME, startTime);

        db.insert(TABLE_NAME, null, contentValues);
        return true;

    }

    public Cursor getData(String timerId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME_ENTRYID + " =" + timerId, null);
        return res;
    }

    /**
     * Method for udpating the status in an error report with a specific error id
     * @param timerId unique ID for the error report which to update
     */
    public void updateTimer(String timerId, String startTime){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_START_TIME, startTime);

        db.update(TABLE_NAME, contentValues, COLUMN_NAME_ENTRYID+" = ? ", new String[]{timerId});

    }

}