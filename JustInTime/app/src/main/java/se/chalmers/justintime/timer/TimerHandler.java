package se.chalmers.justintime.timer;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.database.TimerLogEntry;
import se.chalmers.justintime.timer.timers.TimerInstance;

import static se.chalmers.justintime.timer.TimerService.ALERT_TIMER;
import static se.chalmers.justintime.timer.TimerService.UPDATED_TIME;

/**
 * Created by David on 2017-04-20.
 */

public class TimerHandler implements Ticker {

    private List<TimerInstance> timers;
    private ScheduledThreadPoolExecutor timerSchedulerExecutor;
    private DatabaseHelper databaseHelper;
    private Messager messager;


    public TimerHandler(Messager messager, Context context) {
        this.messager = messager;
        this.timers = new ArrayList<>();
//      The parameter indicates the maximum allowed number of threads/timers
        timerSchedulerExecutor = new ScheduledThreadPoolExecutor(100);
        databaseHelper = DatabaseHelper.getInstance(context);

    }

    public void addTimer(TimerInstance timerInstance) {
        timers.add(timerInstance);
    }

    public void addTimer(Integer id, ArrayList<Long> durations) {
        TimerInstance timerInstance = new TimerInstance(id, durations, this);
        timers.add(timerInstance);
    }

    public void startTimer(int id) {
        TimerInstance instance = findById(id);
        if(instance != null){
            instance.setSendingUpdates(true);
            scheduleTimerWithFixedRate(instance, 20);
        }
        else {
            Log.d("TimerHandler", "Tried to start timer that did not exist id: " + id);
        }
    }

    private void scheduleTimerWithFixedRate(TimerInstance timer, long fixedRateInMilliSeconds) {
        ScheduledFuture sf = timerSchedulerExecutor.scheduleAtFixedRate(timer, 0, fixedRateInMilliSeconds, TimeUnit.MILLISECONDS);
        timer.start(sf);
    }

    public boolean removeTimer(int timerId) {
        TimerInstance timerInstance = findById(timerId);
        if (timerInstance != null) {
            return pauseTimer(timerId) && timers.remove(findById(timerId));
        }
        return false;
    }

    public boolean pauseTimer(int timerId) {
        TimerInstance t = findById(timerId);
        if(t != null){
            TimerLogEntry entry = new TimerLogEntry(timerId, t.getStartTime(), t.getDuration());
            databaseHelper.insertTimerData(entry);
            return t.stop();
        }
        return false;
    }

    private TimerInstance findById(int id){
        for (TimerInstance t: timers) {
            if(t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public long resetTimer(int timerId) {
        TimerInstance t = findById(timerId);
        t.reset();
        onTick(t.getRemainingTime());
        return t.getRemainingTime();
    }

    /**
     * Tells all the timers to stop sending periodic updates
     */
    public void stopSendingUpdates() {
        for (TimerInstance t : timers) {
            t.setSendingUpdates(false);
        }
    }

    public void startSendingUpdates(int id) {
        TimerInstance t = findById(id);
        if (t != null) {
            t.setSendingUpdates(true);
        }
    }


    @Override
    public void onTick(long time){
        Message message = Message.obtain(null, TimerService.UPDATE_TIMER);
        message.getData().putSerializable(UPDATED_TIME, time);
        messager.sendMessage(message);
    }

    @Override
    public void onFinish(TimerInstance timerInstance) {
        messager.sendMessage(Message.obtain(null, ALERT_TIMER));
        messager.showNotification(2, "Timer has finished");
        TimerLogEntry entry = new TimerLogEntry(timerInstance.getId(), timerInstance.getStartTime(), timerInstance.getDuration());
        databaseHelper.insertTimerData(entry);
    }
}
