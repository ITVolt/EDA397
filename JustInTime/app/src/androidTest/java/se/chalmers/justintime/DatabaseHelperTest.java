package se.chalmers.justintime;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import se.chalmers.justintime.database.TimerLogEntry;

import static org.junit.Assert.*;

/**
 * Created by David on 2017-04-04.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        databaseHelper = new DatabaseHelper(appContext);
    }

    public void tearDown() throws Exception{

    }

    @Test
    public void onCreate() throws Exception {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        databaseHelper.onCreate(db);
        assertNotNull(db);
    }

    @Test
    public void insertTimer() throws Exception {
//        TimerLogEntry entry = new TimerLogEntry(1 ,1, LocalDateTime.now(), 1);
//        databaseHelper.insertTimer(entry);
//        TimerLogEntry timerLogEntry = databaseHelper.getEntryById(1);
//        assertTrue(timerLogEntry.getGroupId() == 3);
    }


    @Test
    public void updateTimer() throws Exception {
//        TimerLogEntry entry = new TimerLogEntry(1 ,1, LocalDateTime.now(), 1);
//        databaseHelper.insertTimer(entry);
//        entry.setGroupId(2);
//        databaseHelper.updateTimer(entry);
    }

}