package se.chalmers.justintime.timer;

import se.chalmers.justintime.timer.timers.AbstractTimer;

/**
 * Created by David on 2017-04-20.
 */

public interface TimerCallback {
    void nextTimer(AbstractTimer current, AbstractTimer nextTimer);
    void previousTimer(AbstractTimer current, AbstractTimer previousTimer);
}
