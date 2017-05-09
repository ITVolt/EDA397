package se.chalmers.justintime;

import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.fragments.TimerFragment;
import se.chalmers.justintime.timer.TimerService;

/**
 * Created by Patrik on 2017-04-04.
 */

public class Presenter {

    private TimerFragment timerFragment;
    private Messenger timerService;
    private Map<Integer, Boolean> states;
    private int timerAid;
    private DatabaseHelper databaseHelper;


    public Presenter(TimerFragment fragment, Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
        this.timerFragment = fragment;
        states = new HashMap<>(100);
    }

    public int newTimer(String label, String[] tags, ArrayList<Long> durations){
        Log.d("Presenter", "newTimer");
        int id = databaseHelper.insertTimer(label, tags);
        Message message = Message.obtain(null, TimerService.NEW_TIMER, id, 0);
        message.getData().putSerializable(TimerService.TIMER_DURATIONS, durations);
        sendMessage(message);
        return id;
    }

    public void updateTimer(long time){
        timerFragment.updateTime(time);
    }

    public void setTimerService(Messenger timerService) {
        this.timerService = timerService;
    }

    private void sendMessage(Message message){
        if(timerService != null){
            try{
                timerService.send(message);
            } catch (RemoteException e) {
                Log.d("Presenter", "RemoteException " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e){
                Log.d("Presenter", "Exception " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void startTimer(int id) {
        states.put(id, true);
        Log.d("Presenter", "startTimer");
        Message message = Message.obtain(null, TimerService.START_TIMER, id, 0);
        sendMessage(message);
    }
    public void pauseTimer(int id){
        states.put(id, false);
        Log.d("Presenter", "puaseTimer");
        Message message = Message.obtain(null, TimerService.PAUSE_TIMER, id, 0);
        sendMessage(message);
    }
    public void resetTimer(int id){
        sendMessage(Message.obtain(null, TimerService.RESET_TIMER, id, 0));
    }
    public void removeTimer(int timerId) {
        sendMessage(Message.obtain(null, TimerService.REMOVE_TIMER, timerId, 0));
    }

    public void alert(){
        timerFragment.onTimerFinish();
    }

    public void stopSendingUpdates(){
        sendMessage(Message.obtain(null, TimerService.STOP_SENDING_UPDATES));
    }
    public void startSendingUpdates(int id){
        sendMessage(Message.obtain(null, TimerService.START_SENDING_UPDATES, id, 0));
    }

    public void setFragment(TimerFragment fragment) {
        this.timerFragment = fragment;
    }

    public boolean getRunningState(int id) {
        return states.get(id) != null && states.get(id);
    }

    public void setTimerId(int timerId) {
        timerAid = timerId;
        states.put(timerId, false);
        timerFragment.setTimerId(timerId);
    }

    public void setAid() {
        timerFragment.setTimerId(timerAid);
    }

    public void updateTimerLabel(int timerId, String label) {
        databaseHelper.updateTimerLabel(timerId, label);
    }

    public void setTimerTags(int timerId, String[] newTags) {
        databaseHelper.setTimerTags(timerId, newTags);
    }
}
