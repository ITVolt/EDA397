package se.chalmers.justintime;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import se.chalmers.justintime.fragments.TimerFragment;
import se.chalmers.justintime.timer.ParcelableTimer;
import se.chalmers.justintime.timer.TimerService;

/**
 * Created by Patrik on 2017-04-04.
 */

public class Presenter {


    private TimerFragment timerFragment;
    private Messenger timerService;
    private int nextTimerId = 1; //TODO this should probably not be here

    public Presenter(TimerFragment fragment) {
        this.timerFragment = fragment;
    }

    public int newTimer(ArrayList<Long> durations){
        Log.d("Presenter", "newTimer");
        ParcelableTimer timer = new ParcelableTimer(nextTimerId++, durations);
        Message message = Message.obtain(null, TimerService.NEW_TIMER);
        message.getData().putParcelable(TimerService.NEW_TIMER_INFO, timer);
        sendMessage(message);
        return  timer.getId();
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
        Log.d("Presenter", "startTimer");
        Message message = Message.obtain(null, TimerService.START_TIMER, id, 0);
        sendMessage(message);
    }
    public void pauseTimer(int id){
        Log.d("Presenter", "puaseTimer");
        Message message = Message.obtain(null, TimerService.PAUSE_TIMER, id, 0);
        sendMessage(message);
    }
    public void resetTimer(int id){
        sendMessage(Message.obtain(null, TimerService.RESET_TIMER, id, 0));
    }
}
