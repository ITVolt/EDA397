package se.chalmers.justintime.alert;

import android.content.Context;

/**
 * Created by David on 2017-04-01.
 */

public class AlarmBuilder {
    private final Context context;
    private boolean useSound, useVibration;

    public AlarmBuilder(Context context) {
        this.context = context;
    }

    public void setUseSound(boolean useSound) {
        this.useSound = useSound;
    }

    public void setUseVibration(boolean useVibration) {
        this.useVibration = useVibration;
    }

    public Alarm getAlarmInstance() {
        Alarm alarm = new BasicAlarm();
        if (useSound) {
            alarm = new SoundAlarm(alarm, context);
        }
        if (useVibration) {
            alarm = new VibrationAlarm(alarm, context);
        }
        return alarm;
    }

}
