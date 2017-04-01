package se.chalmers.justintime.alert;

/**
 * Created by David on 2017-04-01.
 */

public class BasicAlarm implements Alarm {
    private boolean alarmBroadcast = false;

    @Override
    public void alert() {
        setAlarmBroadcast(true);
    }

    @Override
    public void dismissAlarm() {
        setAlarmBroadcast(false);
    }

    public boolean isAlarmBroadcasted() {
        return alarmBroadcast;
    }

    public void setAlarmBroadcast(boolean status) {
        this.alarmBroadcast = status;
    }
}
