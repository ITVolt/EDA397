package se.chalmers.justintime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by David on 2017-04-04.
 */
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

    }

    @Test
    public void getData() throws Exception {

    }

    @Test
    public void updateTimer() throws Exception {

    }

}