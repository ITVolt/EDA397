package se.chalmers.justintime.timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;

import se.chalmers.justintime.R;
import se.chalmers.justintime.activities.MainActivity;

/**
 * Created by Nieo on 08/04/17.
 */

public class TimerService extends Service implements Messager {

    private TimerHandler timerHandler = new TimerHandler(this, this);
    private NotificationManager notificationManager;
    private Messenger client;

    private final static int ONGOING_NOTIFICATION = 1;

    private boolean serviceInForeground = false;

    /**
     * For testing purposes only
     */
    public final static int ECHO = 0;

    /**
     * Set the receiver of the updates from the TimerSerivce
     */
    public final static int REGISTER_CLIENT = 1;
    /**
     * Run the TimerService in the foreground
     */
    public final static int ENTER_FOREGROUND = 2;
    /**
     * Stop the TimerService from running in the foreground
     */
    public final static int LEAVE_FOREGROUND = 3;

    /**
     * Removes a timer from the timerhandler
     * arg1: id of timer to remove
     */
    public static final int REMOVE_TIMER = 9;

    /**
     * TimerService creates a new timer
     * arg1: id of new timer
     */
    public final static int NEW_TIMER = 10;
    public static final String TIMER_DURATIONS = "TIMER_DURATIONS";
    /**
     * TimerService starts a timer
     * arg1: id of timer
     */
    public final static int START_TIMER = 11;
    /**
     * TimerService stops a timer
     * arg1: id of timer
     */
    public final static int PAUSE_TIMER = 12;
    /**
     * TimerService resets a timer to its default value
     * arg1: id of timer
     */
    public final static int RESET_TIMER = 13;
    /**
     * Timer starts sending periodic messages about its state to the client
     * arg1: id of timer
     */
    public final static int START_SENDING_UPDATES = 14;

    public final static int STOP_SENDING_UPDATES = 15;

    /**
     * Id of a newly created timer
     * arg1: id
     */
    public static final int TIMER_ID = 16;

    /**
     * Notifiy client that the time have changed
     */
    public final static int UPDATE_TIMER = 20;
    public final static String UPDATED_TIME = "UPDATED_TIME";
    /**
     * Notify client that a timer have finnished
     */
    public final static int ALERT_TIMER = 21;



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
                    Log.d("TimerService", "Setting client " + client);
                    break;
                case ENTER_FOREGROUND:
                    serviceInForeground = true;
                    timerHandler.stopSendingUpdates();
                    // showForegroundNotification();
                    break;
                case LEAVE_FOREGROUND:
                    serviceInForeground = false;
                    // stopForeground(true);
                    break;
                case REMOVE_TIMER:
                    timerHandler.removeTimer(message.arg1);
                    break;
                case NEW_TIMER:
                    final Bundle bundle = message.getData();
                    bundle.setClassLoader(getClassLoader());
                    ArrayList<Long> durations = (ArrayList<Long>) bundle.getSerializable(TIMER_DURATIONS);
                    timerHandler.addTimer(message.arg1, durations);
                    break;
                case START_TIMER:
                    timerHandler.startTimer(message.arg1);
                    break;
                case PAUSE_TIMER:
                    timerHandler.pauseTimer(message.arg1);
                    break;
                case RESET_TIMER:
                    timerHandler.resetTimer(message.arg1);
                    break;
                case START_SENDING_UPDATES:
                    timerHandler.startSendingUpdates(message.arg1);
                    break;
                case STOP_SENDING_UPDATES:
                    timerHandler.stopSendingUpdates();
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
        AndroidThreeTen.init(this);
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        notificationManager.cancel(R.string.remote_service_started);

    }

    public int onStartCommand(Intent intent, int flags, int startId){

        return START_STICKY;
    }

    /**
     * Show a notification while this service is running.
     */
    public void showNotification(int notificationId, CharSequence text) {
        if(serviceInForeground){
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
            notificationManager.notify(notificationId, notification);
        }
    }

    private void showForegroundNotification(){
        // The PendingIntent to launch our activity if the user selects this notification
        //TODO move to the timer fragment insted of the last used fragment
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        //TODO fix the content of the notification
        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)  // the status icon
                .setWhen(System.currentTimeMillis()+10000)  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setUsesChronometer(true)
                .setContentText("Change me")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        //notificationManager.notify(R.string.remote_service_started, notification);
        startForeground(ONGOING_NOTIFICATION, notification);
    }


    public void sendMessage(Message message) {
        if (client != null) {
            try {
                client.send(message);
            } catch (RemoteException e) {
                Log.d("TimerService", "onTick RemoteExeption " + e.getMessage());
                e.printStackTrace();
                Log.d("TimerService", "Got a remote execption setting client to null");
                client = null;
            } catch (Exception e){
                Log.d("TimerService", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
