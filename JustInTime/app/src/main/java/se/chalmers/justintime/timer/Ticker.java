package se.chalmers.justintime.timer;

import se.chalmers.justintime.timer.timers.TimerInstance;

/**
 * Created by David on 2017-04-20.
 */

public interface Ticker {
    void onTick(long time);

    void onFinish(TimerInstance timerInstance);
}
