package se.chalmers.justintime.alert;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import se.chalmers.justintime.R;
import se.chalmers.justintime.fragments.TimerFragment;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Abel Asefa on 4/11/2017.
 */

public class Notification {
    NotificationCompat.Builder mBuilder;
    PendingIntent resultPendingIntent;
    Context context;
    Intent resultIntent;

    public Notification(Context context){
        this.context = context;
    }
    public void myNotification() {
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_menu_camera)
                .setContentTitle("Alarm Notification")
                .setContentText("Time Has Ended");
    }
    public void pressNotification(){
        //Open the following activity when the Notification is pressed
        resultIntent = new Intent(context, TimerFragment.class);
        resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
    }
    public void showNotification(){
        pressNotification();
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
