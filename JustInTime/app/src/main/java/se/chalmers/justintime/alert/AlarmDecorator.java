package se.chalmers.justintime.alert;

import android.content.Context;

/**
 * Created by David on 2017-04-01.
 */

public class AlarmDecorator implements Alarm {
    private Alarm alarm;

    private Context context;

    public AlarmDecorator(Alarm alarm, Context context) {
        this.alarm = alarm;
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void alert() {
        this.alarm.alert();
    }

    @Override
    public void dismissAlarm() {
        this.alarm.dismissAlarm();
    }
}
