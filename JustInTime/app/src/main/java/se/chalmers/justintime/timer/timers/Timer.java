package se.chalmers.justintime.timer.timers;

/**
 * Created by David on 2017-04-08.
 */

public interface Timer {

    public void start();

    public void pause();

    public void resume();

    public void stop();

    public long getRemainingTime();

    public void onFinish();
}
