package se.chalmers.justintime.alert;

import org.junit.Test;

import se.chalmers.justintime.Timer;

import static junit.framework.Assert.assertEquals;

/**
 * Created by JonasPC on 2017-04-02.
 */

public class TimerTest {


    @Test
    public void timeCheck(){

        Timer t = new Timer();
        t.start();
        t.stop();
        assertEquals(0, t.getElapsedTime());

    }


}
