package se.chalmers.justintime.gui.activities;

/**
 * Functionality common for all activities with timers or stopwatches.
 * Created by Patrik on 2017-04-02.
 */

public interface CounterActivity {
    void start();
    void pause();
    void reset();
    void updateTime(long ms);
}
