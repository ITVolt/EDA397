package se.chalmers.justintime;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.justintime.fragments.TimerFragment;
import se.chalmers.justintime.timer.TimerService;

/**
 * Created by Patrik on 2017-04-04.
 */

public class Presenter {


    private TimerFragment timerFragment;
    private Messenger timerService;
    public Map<Integer, Boolean> states;
    public int timerAid;


    public Presenter(TimerFragment fragment) {
        this.timerFragment = fragment;
        states = new HashMap<>(100);
    }

    public void newTimer(String name, String[] tags, ArrayList<Long> durations){
        Log.d("Presenter", "newTimer");
        Message message = Message.obtain(null, TimerService.NEW_TIMER);
        message.getData().putSerializable(TimerService.TIMER_NAME, name);
        message.getData().putSerializable(TimerService.TIMER_TAGS, tags);
        message.getData().putSerializable(TimerService.TIMER_DURATIONS, durations);
        sendMessage(message);
    }

    public void updateTimer(long time){
        timerFragment.updateTime(time);
    }

    public void setTimerService(Messenger timerService) {
        this.timerService = timerService;
        //TODO remove this when you can add timers
        ArrayList<Long> duration = new ArrayList<>();
        duration.add(90000L);
        String[] tags = new String[]{"A tag"};
        newTimer("A Timer", tags, duration);
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
        timerFragment.setTimerFragmentId(timerId);
    }

    public void setAid() {
        timerFragment.setTimerFragmentId(timerAid);
    }
}
