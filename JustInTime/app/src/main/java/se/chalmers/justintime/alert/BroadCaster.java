package se.chalmers.justintime.alert;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Abel Asefa on 4/11/2017.
 */
public class BroadCaster extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification = new Notification(context);
        notification.newNotification(intent);
    }
}

