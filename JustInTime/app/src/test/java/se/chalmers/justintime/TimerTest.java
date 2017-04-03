package se.chalmers.justintime;

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

    @Test
    public void pauseCheck() {

        Timer t = new Timer();
        t.start();
        t.pause();
        for(int i = 1000; i>0; i--){
            System.out.println(i);
        }
        t.resume();
        t.stop();

        assertEquals(0,t.getElapsedTime());
    }


}
