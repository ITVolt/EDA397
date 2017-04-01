package se.chalmers.justintime.alert;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by David on 2017-04-01.
 */

public class VibrationAlarm extends AlarmDecorator {
    private final Vibrator vibrator;
    private int vibrationTimeInMS = 1000;

    public VibrationAlarm(Alarm alarm, Context context) {
        super(alarm, context);
        vibrator = (Vibrator) super.getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void alert() {
        super.alert();
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationTimeInMS);
        }
    }

    @Override
    public void dismissAlarm() {
        super.dismissAlarm();
        vibrator.cancel();
    }
}
