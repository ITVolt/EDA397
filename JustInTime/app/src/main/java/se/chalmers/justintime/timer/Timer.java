package se.chalmers.justintime.timer;

/**
 * Created by David on 2017-04-08.
 */

public interface Timer {

    public void start();

    public void pause();

    public void resume();

    public void stop();

    public void onFinish();
}
