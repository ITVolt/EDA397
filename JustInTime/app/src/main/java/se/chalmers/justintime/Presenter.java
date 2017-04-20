package se.chalmers.justintime;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import se.chalmers.justintime.fragments.TimerFragment;
import se.chalmers.justintime.timer.TimerHandler;
import se.chalmers.justintime.timer.TimerService;

/**
 * Created by Patrik on 2017-04-04.
 */

public class Presenter {


    private TimerFragment timerFragment;
    private Messenger timerService;
    public Presenter(TimerFragment fragment) {
        this.timerFragment = fragment;


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
                e.printStackTrace();
            }
        }
    }

    public void startTimer() {
        Message message = Message.obtain(null, TimerService.START_TIMER);
        sendMessage(message);
    }
}
