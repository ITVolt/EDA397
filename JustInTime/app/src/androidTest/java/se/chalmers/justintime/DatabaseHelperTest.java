package se.chalmers.justintime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.database.TimerLogEntry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        AndroidThreeTen.init(appContext);
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
        databaseHelper.resetDatabase();
        TimerLogEntry entry = new TimerLogEntry(1337 ,42, LocalDateTime.now(), 1);
        databaseHelper.insertTimer(entry);
        TimerLogEntry timerLogEntry = databaseHelper.getEntryById(1337);
        assertTrue(timerLogEntry.getGroupId() == 42);
        databaseHelper.resetDatabase();
    }

    @Test
    public void getNextAvailableId() throws Exception {
        databaseHelper.resetDatabase();
        TimerLogEntry entry1 = new TimerLogEntry(1 ,1, LocalDateTime.now(), 60);
        TimerLogEntry entry2 = new TimerLogEntry(2 ,2, LocalDateTime.now(), 120);
        TimerLogEntry entry3 = new TimerLogEntry(3 ,3, LocalDateTime.now(), 180);
        TimerLogEntry entry4 = new TimerLogEntry(4 ,4, LocalDateTime.now(), 240);
        databaseHelper.insertTimer(entry1);
        databaseHelper.insertTimer(entry2);
        databaseHelper.insertTimer(entry3);
        databaseHelper.insertTimer(entry4);
        assertTrue(databaseHelper.getNextAvailableId() == 5);
    }

    @Test
    public void getNextAvailablePauseId() throws Exception {
        databaseHelper.resetDatabase();
        TimerLogEntry entry1 = new TimerLogEntry(1 ,1, LocalDateTime.now(), 60);
        TimerLogEntry entry2 = new TimerLogEntry(2 ,1, LocalDateTime.now(), 120);
        TimerLogEntry entry3 = new TimerLogEntry(3 ,2, LocalDateTime.now(), 180);
        TimerLogEntry entry4 = new TimerLogEntry(4 ,2, LocalDateTime.now(), 240);
        databaseHelper.insertTimer(entry1);
        databaseHelper.insertTimer(entry2);
        databaseHelper.insertTimer(entry3);
        databaseHelper.insertTimer(entry4);
        assertTrue(databaseHelper.getNextAvailablePauseId() == 3);

    }

    @Test
    public void insertEntries() throws Exception {
        databaseHelper.resetDatabase();
        TimerLogEntry entry1 = new TimerLogEntry(1 ,1, LocalDateTime.now(), 60);
        TimerLogEntry entry2 = new TimerLogEntry(2 ,1, LocalDateTime.now(), 120);
        TimerLogEntry entry3 = new TimerLogEntry(3 ,2, LocalDateTime.now(), 180);
        TimerLogEntry entry4 = new TimerLogEntry(4 ,2, LocalDateTime.now(), 240);
        databaseHelper.insertTimer(entry1);
        databaseHelper.insertTimer(entry2);
        databaseHelper.insertTimer(entry3);
        databaseHelper.insertTimer(entry4);
        TimerLogEntry entry = new TimerLogEntry(databaseHelper.getNextAvailableId(), databaseHelper.getNextAvailablePauseId(), LocalDateTime.now(), 30);
        databaseHelper.insertTimer(entry);
        assertTrue(databaseHelper.getEntryById(5).getGroupId() == 3);
        assertTrue(databaseHelper.getEntryById(5).getDuration() == 30);
    }

    @Test
    public void insertFirstEntry() throws Exception {
        databaseHelper.resetDatabase();
        TimerLogEntry entry = new TimerLogEntry(databaseHelper.getNextAvailableId(), databaseHelper.getNextAvailablePauseId(), LocalDateTime.now(), 30);
        databaseHelper.insertTimer(entry);

        TimerLogEntry entry2 = new TimerLogEntry(databaseHelper.getNextAvailableId(), databaseHelper.getNextAvailablePauseId(), LocalDateTime.now(), 30);
        databaseHelper.insertTimer(entry2);

        TimerLogEntry entry3 = new TimerLogEntry(databaseHelper.getNextAvailableId(), databaseHelper.getEntryById(2).getGroupId() , LocalDateTime.now(), 30);
        databaseHelper.insertTimer(entry3);

        assertTrue(databaseHelper.getEntryById(1).getGroupId() == 1);
        assertTrue(databaseHelper.getEntryById(2).getGroupId() == 2);
        assertTrue(databaseHelper.getEntryById(3).getGroupId() == 2);

    }

    @Test
    public void updateTimer() throws Exception {
//        TimerLogEntry entry = new TimerLogEntry(1 ,1, LocalDateTime.now(), 1);
//        databaseHelper.insertTimer(entry);
//        entry.setGroupId(2);
//        databaseHelper.updateTimer(entry);
    }

}