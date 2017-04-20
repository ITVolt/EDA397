package se.chalmers.justintime.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import se.chalmers.justintime.R;
import se.chalmers.justintime.activities.MainActivity;

/**
 * Created by Nieo on 08/04/17.
 */

public class TimerService extends Service {

    private NotificationManager notificationManager;
    private Messenger client;

    public final static int ECHO = 0;
    public final static int REGISTER_CLIENT = 1;
    public final static int ENTER_FOREGROUND = 2;
    public final static int LEAVE_FORGROUND = 3;
    //TODO add messages for interaction with timers

    //TODO add timers

    private class MessagingHandler extends Handler{
        @Override
        public void handleMessage(Message message){
            Log.d("TimerService", "Received message " + message.what);
            switch (message.what){
                case ECHO:
                    Messenger replyTo = message.replyTo;
                    message.replyTo = null;
                    try {
                        replyTo.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case REGISTER_CLIENT:
                    client = message.replyTo;
                    break;
                case ENTER_FOREGROUND:
                    showNotification("Entered foreground");
                    break;
                case LEAVE_FORGROUND:
                    notificationManager.cancel(R.string.remote_service_started);
                    break;
                default:
                    super.handleMessage(message);
            }
        }

    }

    final Messenger messenger = new Messenger(new MessagingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        notificationManager.cancel(R.string.remote_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(CharSequence text) {


        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.remote_service_started, notification);
    }
}