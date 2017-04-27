package se.chalmers.justintime.timer;

/**
 * Created by David on 2017-04-20.
 */

public interface Ticker {
    void onTick(long time);
}
