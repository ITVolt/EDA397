package se.chalmers.justintime;

import android.os.SystemClock;
import android.view.View;

/**
 * Created by Patrik on 2017-04-04.
 */

public class Presenter {

    public static final int REFRESH_RATE = 30;
    public static final int FRAME_LENGTH = 1000/REFRESH_RATE;

    private Timer timer;

    private boolean isStarted;
    private boolean isRunning;

    private View currView;

    public Presenter(View view) {
        currView = view;
    }

    private void updateRunning() {
        boolean running = isStarted;
        if (running != isRunning) {
            if (running) {
                //updateText(SystemClock.elapsedRealtime());
                //dispatchChronometerTick();
                currView.postDelayed(tickRunnable, FRAME_LENGTH);
            } else {
                currView.removeCallbacks(tickRunnable);
            }
            isRunning = running;
        }
    }

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                //updateText(SystemClock.elapsedRealtime());
                //dispatchChronometerTick();
                currView.postDelayed(tickRunnable, FRAME_LENGTH);
            }
        }
    };


}
