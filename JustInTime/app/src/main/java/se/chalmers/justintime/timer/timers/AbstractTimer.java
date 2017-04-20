package se.chalmers.justintime.timer.timers;

import android.os.SystemClock;

/**
 * Created by JonasPC on 2017-04-02.
 */
public abstract class AbstractTimer implements Runnable, Timer {
    private long targetInMilliSeconds;
    private long durationInMilliSeconds;
    private long timeOffset;
    private boolean isRunning;
    private boolean isDone;

    public AbstractTimer(long targetInMilliSeconds) {
        this.targetInMilliSeconds = targetInMilliSeconds;
    }

    @Override
    public void run() {
        this.update();
    }

    public void update() {
        long milliSeconds = calculateOffset();
        durationInMilliSeconds += milliSeconds;
        if (isTimerDone()) {
            this.onFinish();
        }
    }

    public boolean isDone() {
        return isDone;
    }

    private long calculateOffset() {
        long currentTime = SystemClock.elapsedRealtime();
        long diff = 0;
        if (timeOffset != 0) {
            diff = currentTime - timeOffset;
        }
        timeOffset = currentTime;
        return diff;
    }

    private boolean isTimerDone() {
        return targetInMilliSeconds - durationInMilliSeconds < 0;
    }

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void pause() {
        isRunning = false;
    }

    @Override
    public void resume() {
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
        durationInMilliSeconds = 0;
    }

    @Override
    public long getRemainingTime() {
        return targetInMilliSeconds - durationInMilliSeconds;
    }

    @Override
    public void onFinish() {
        isDone = true;
    }
}


