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
import org.threeten.bp.ZoneOffset;

import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.database.TimerInfoBundle;
import se.chalmers.justintime.database.TimerLogEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by David on 2017-04-04.
 * Updated by Patrik on 2017-05-04.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        databaseHelper = DatabaseHelper.getInstance(appContext);
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
        try {
            databaseHelper.insertTimer(null, null);
            fail(); //Should throw exception for null parameters.
        } catch (Exception ignored) {}
        try {
            databaseHelper.insertTimer(null, new String[]{"not null"});
            fail(); //Should throw exception for null parameters.
        } catch (Exception ignored) {}
        try {
            databaseHelper.insertTimer("not null", null);
            fail(); //Should throw exception for null parameters.
        } catch (Exception ignored) {}
        try {
            databaseHelper.insertTimer("", new String[]{"not empty"});
            fail(); //Should throw exception for empty parameters.
        } catch (Exception ignored) {}
        assertEquals("Id of timer test: ", 1, databaseHelper.insertTimer("timer 1", new String[]{"Undefined"}));
        assertEquals("Id of timer test: ", 2, databaseHelper.insertTimer("timer 2", new String[]{"Undefined"}));
        assertEquals("Id of timer test: ", 3, databaseHelper.insertTimer("timer 2", new String[]{"Undefined"}));
    }

    @Test
    public void readTimerLabel() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("timer 1", new String[]{"Undefined"});
        assertEquals("Normal conditions", "timer 1", databaseHelper.getTimerLabel(id));
        id = databaseHelper.insertTimer("Undefined", new String[]{"Undefined"});
        assertEquals("Empty string", "Undefined", databaseHelper.getTimerLabel(id));
    }

    @Test
    public void updateTimerLabel() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("old timer", new String[]{"Undefined"});
        assertEquals("Normal conditions", "old timer", databaseHelper.getTimerLabel(id));
        databaseHelper.updateTimerLabel(id, "new timer");
        assertEquals("Changed label", "new timer", databaseHelper.getTimerLabel(id));
        try {
            databaseHelper.updateTimerLabel(id, null);
            fail(); //Should throw exception for null parameters.
        } catch (Exception ignored) {}
        try {
            databaseHelper.updateTimerLabel(id, "");
            fail(); //Should throw exception for empty parameters.
        } catch (Exception ignored) {}
    }

    @Test
    public void setTimerTags() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("Undefined", new String[]{"Tag 1"});
        String[] tags = databaseHelper.getTimerTags(id);
        assertEquals("1 tag", 1, tags.length);
        databaseHelper.setTimerTags(id, new String[]{"Tag 2", "Tag 3"});
        tags = databaseHelper.getTimerTags(id);
        assertEquals("1 tag", 2, tags.length);
        assertEquals("changed tag", "Tag 2", tags[0]);
        assertEquals("changed tag", "Tag 3", tags[1]);
    }

    @Test
    public void readTimerTags() throws Exception {
        int id = databaseHelper.insertTimer("Undefined", new String[]{"Tag 1"});
        String[] tags = databaseHelper.getTimerTags(id);
        assertEquals("1 tag", 1, tags.length);
        assertEquals("Tag 1", tags[0]);
        id = databaseHelper.insertTimer("Undefined", new String[]{"Tag 1", "Tag 2"});
        tags = databaseHelper.getTimerTags(id);
        assertEquals("2 tags", 2, tags.length);
        assertEquals("Tag 1", tags[0]);
        assertEquals("Tag 2", tags[1]);
    }

    @Test
    public void insertTimerData() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("Undefined", new String[]{"Undefined"});
        TimerLogEntry entry = new TimerLogEntry(id, LocalDateTime.now(), 1);
        databaseHelper.insertTimerData(entry);
        TimerInfoBundle timerInfo = databaseHelper.getTimerInfo(id);
        assertEquals(id, timerInfo.getId());
        databaseHelper.resetDatabase();
    }

    @Test
    public void insertEntries() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("Undefined", new String[]{"Undefined"});
        TimerLogEntry entry1 = new TimerLogEntry(id, LocalDateTime.now(), 60);
        TimerLogEntry entry2 = new TimerLogEntry(id, LocalDateTime.now(), 120);
        id = databaseHelper.insertTimer("Undefined", new String[]{"Undefined"});
        TimerLogEntry entry3 = new TimerLogEntry(id, LocalDateTime.now(), 180);
        TimerLogEntry entry4 = new TimerLogEntry(id, LocalDateTime.now(), 240);
        databaseHelper.insertTimerData(entry1);
        databaseHelper.insertTimerData(entry2);
        databaseHelper.insertTimerData(entry3);
        databaseHelper.insertTimerData(entry4);
        id = databaseHelper.insertTimer("Undefined", new String[]{"Undefined"});
        TimerLogEntry entry = new TimerLogEntry(id, LocalDateTime.now(), 30);
        databaseHelper.insertTimerData(entry);
        assertEquals(3, databaseHelper.getTimerInfo(id).getId());
        assertEquals(30, databaseHelper.getTimerInfo(id).getTotalDuration());
    }

    @Test
    public void getTimerData() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("Timer A", new String[]{"Studies", "Calculus"});
        LocalDateTime date = LocalDateTime.now();
        TimerLogEntry entry = new TimerLogEntry(id, date, 30);
        databaseHelper.insertTimerData(entry);
        TimerInfoBundle timerInfoBundle = databaseHelper.getTimerInfo(id);
        assertEquals("Label", "Timer A", timerInfoBundle.getLabel());
        assertEquals("Tag", "Studies", timerInfoBundle.getTags()[0]);
        assertEquals("Tag", "Calculus", timerInfoBundle.getTags()[1]);
        assertEquals("Date", date.toEpochSecond(ZoneOffset.UTC), timerInfoBundle.getTimes().get(0).first.toEpochSecond(ZoneOffset.UTC));
        assertEquals("Duration", Long.valueOf(30), timerInfoBundle.getTimes().get(0).second);
    }

    @Test
    public void getAllTimerInfoTest() throws Exception {
        databaseHelper.resetDatabase();
        int id;
        TimerLogEntry entry;
        LocalDateTime date = LocalDateTime.now();
        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, date, 5000);
        databaseHelper.insertTimerData(entry);
        id = databaseHelper.insertTimer("Timer B", new String[]{"Undefined"});
        entry = new TimerLogEntry(id, date, 6000);
        databaseHelper.insertTimerData(entry);
        id = databaseHelper.insertTimer("Productivity", new String[]{"Studies", "Calculus"});
        entry = new TimerLogEntry(id, date.minusDays(2), 15000);
        databaseHelper.insertTimerData(entry);
        entry = new TimerLogEntry(id, date.minusDays(1), 20000);
        databaseHelper.insertTimerData(entry);
        TimerInfoBundle[] timers = databaseHelper.getAllTimerInfo();
        assertEquals("Numbers of unique timers", 3, timers.length);

        assertEquals("Label", "Timer A", timers[0].getLabel());
        assertEquals("Tag", "Other", timers[0].getTags()[0]);
        assertEquals("Date", date.toEpochSecond(ZoneOffset.UTC), timers[0].getTimes().get(0).first.toEpochSecond(ZoneOffset.UTC));
        assertEquals("Duration", Long.valueOf(5000), timers[0].getTimes().get(0).second);

        assertEquals("Label", "Timer B", timers[1].getLabel());
        assertEquals("Tag", 1, timers[1].getTags().length);
        assertEquals("Date", date.toEpochSecond(ZoneOffset.UTC), timers[1].getTimes().get(0).first.toEpochSecond(ZoneOffset.UTC));
        assertEquals("Duration", Long.valueOf(6000), timers[1].getTimes().get(0).second);

        assertEquals("Label", "Productivity", timers[2].getLabel());
        assertEquals("Tag", "Studies", timers[2].getTags()[0]);
        assertEquals("Tag", "Calculus", timers[2].getTags()[1]);
        assertEquals("Date", date.minusDays(2).toEpochSecond(ZoneOffset.UTC), timers[2].getTimes().get(0).first.toEpochSecond(ZoneOffset.UTC));
        assertEquals("Date", date.minusDays(1).toEpochSecond(ZoneOffset.UTC), timers[2].getTimes().get(1).first.toEpochSecond(ZoneOffset.UTC));
        assertEquals("Duration", 35000, timers[2].getTotalDuration());
        assertEquals("Duration", Long.valueOf(15000), timers[2].getTimes().get(0).second);
        assertEquals("Duration", Long.valueOf(20000), timers[2].getTimes().get(1).second);
    }

    @Test
    public void populateWithMockValues() throws Exception {
        databaseHelper.resetDatabase();
        int id = databaseHelper.insertTimer("Timer A", new String[]{"Running"});
        TimerLogEntry entry = new TimerLogEntry(id, LocalDateTime.now(), 50000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other1"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(1), 15000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other1"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(2), 20000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other1"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(3), 10000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other2"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(4), 11000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(5), 12000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other2"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(6), 13000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other2"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(7), 4000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(8), 8000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(9), 9000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other10"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(10), 14000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(11), 17000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other30"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(12), 22000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other3"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(13), 13000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(14), 5000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(15), 7000);
        databaseHelper.insertTimerData(entry);

        id = databaseHelper.insertTimer("Timer A", new String[]{"Other"});
        entry = new TimerLogEntry(id, LocalDateTime.now().minusDays(16), 5000);
        databaseHelper.insertTimerData(entry);
    }

    @Test
    public void clearDatabase() throws Exception {
        databaseHelper.resetDatabase();
    }

}