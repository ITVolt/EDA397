package se.chalmers.justintime.fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;

import se.chalmers.justintime.Presenter;
import se.chalmers.justintime.R;
import se.chalmers.justintime.activities.CounterActivity;
import se.chalmers.justintime.alert.Alarm;
import se.chalmers.justintime.alert.AlarmBuilder;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.database.TimerLogEntry;

/**
 * This fragment shows a basic timer counting down from a time to zero.
 * Created by Patrik on 2017-04-01.
 */

public class TimerFragment extends Fragment implements CounterActivity {

    private long startValue;
    public static long currentTimerValue;
    private long previousDuration;
    private LocalDateTime startTime;

    private boolean isTimerRunning = false;
    private SharedPreference preferences;

    private TextView timerText;

    private Button startPauseTimerButton;
    private Button resetTimerButton;

    private Alarm alarm;
    private StringBuilder strBuilder = new StringBuilder(8);

    private DatabaseHelper databaseHelper;
    private int timerId;

    private View view;


    private Presenter presenter;


    private long timeCountInMilliSeconds;

    private ProgressBar progressBarCircle;
    private CountDownTimer countDownTimer;
    private int currentTimerId;


    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timer, container, false);

        timerText = (TextView) view.findViewById(R.id.basicTimerTV);

        startPauseTimerButton = (Button) view.findViewById(R.id.timerStartPauseButton);
        resetTimerButton = (Button) view.findViewById(R.id.timerResetButton);

        // Set up the alarm.
        preferences = new SharedPreference(view.getContext());
        AlarmBuilder ab = new AlarmBuilder(view.getContext());
        ab.setUseSound(true);
        ab.setUseVibration(true);
        alarm = ab.getAlarmInstance();

        setButtonOnClickListeners();
        databaseHelper = new DatabaseHelper(this.getContext());
        timerId = databaseHelper.insertTimer("Basic timer", new String[]{"Undefined"});
        progressBarCircle = (ProgressBar) view.findViewById(R.id.progressBarCircle);
        startValue = 90000;
        timeCountInMilliSeconds = startValue;
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();

        return view;
    }

    private void timerStart() {
        setTimerValues();
        setProgressBarValues();
        startCountDownTimer();
    }

    private void timerPause(){
        stopCountDownTimer();
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //progressBarCircle.setProgress((int) (millisUntilFinished/ 1000));

            }

            @Override
            public void onFinish() {
                //reset();
            }

        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();

    }

    private void resetCountDownTimer() {
        countDownTimer.cancel();
        resetProgressBarValues();
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) startValue);
        progressBarCircle.setProgress((int) (timeCountInMilliSeconds) );
    }

    private void setTimerValues() {
        timeCountInMilliSeconds = currentTimerValue + 1000;
    }

    private void resetProgressBarValues() {
        progressBarCircle.setProgress((int) startValue);
    }

    @Override
    public void start() {
        setRunningState(true);
        //FIXME remove this when gui can add new timers
        if(currentTimerId == 0){
            ArrayList<Long> duration = new ArrayList<>();
            duration.add(startValue);
            currentTimerId = presenter.newTimer(duration);
        }
        presenter.startTimer(currentTimerId);
        timerStart();
    }

    @Override
    public void pause() {
        presenter.pauseTimer(currentTimerId);
        setRunningState(false);
        timerPause();
        long duration = startValue - currentTimerValue - previousDuration;
        previousDuration = previousDuration + duration;
        TimerLogEntry entry = new TimerLogEntry(databaseHelper.getNextAvailableId(), timerId, startTime, duration);
        databaseHelper.insertTimerData(entry);
    }

    @Override
    public void reset() {
        if (!isTimerRunning) {
            enableStartPauseTimerButton();
        }
        setRunningState(false);
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();
        previousDuration = 0;
        resetCountDownTimer();
        presenter.resetTimer(currentTimerId);
    }

    @Override
    public void updateTime(long ms) {
        Log.d("UPDATE TIME", ms + " ");
        currentTimerValue = ms;
        updateTimerText();
        progressBarCircle.setProgress((int) (ms));
    }

    private void updateTimerText() {
        timerText.setText(parseTime(currentTimerValue));
    }

    public void onTimerFinish() {
        Log.d("TimerFragment", "onTimerFinish: Time's up!");
        alarm.alert();
        startPauseTimerButton.setText(R.string.timer_button_stop);
        disableStartPauseTimerButton();
        presenter.pauseTimer(currentTimerId);
        setRunningState(false);
        long duration = startValue - currentTimerValue - previousDuration;
        TimerLogEntry entry = new TimerLogEntry(databaseHelper.getNextAvailableId(), timerId, startTime, duration);
        databaseHelper.insertTimerData(entry);
    }

    private void setRunningState(boolean run) {
        if (run) {
            startTime = LocalDateTime.now();
            startPauseTimerButton.setText(R.string.timer_button_pause);
            disableResetButton();
            isTimerRunning = true;
        } else {
            startPauseTimerButton.setText(R.string.timer_button_start);
            enableResetButton();
            isTimerRunning = false;
        }
    }

    private void setButtonOnClickListeners() {
        startPauseTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pause();
                } else {
                    start();
                }
            }
        });
        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void enableResetButton() {
        resetTimerButton.setEnabled(true);
        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.fade_in);
        animator.setTarget(resetTimerButton);
        animator.start();
    }

    private void disableResetButton() {
        resetTimerButton.setEnabled(false);
        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.fade_out);
        animator.setTarget(resetTimerButton);
        animator.start();
    }

    private void disableStartPauseTimerButton() {
        startPauseTimerButton.setEnabled(false);
        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.fade_out);
        animator.setTarget(startPauseTimerButton);
        animator.start();
    }

    private void enableStartPauseTimerButton() {
        startPauseTimerButton.setEnabled(true);
        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.fade_in);
        animator.setTarget(startPauseTimerButton);
        animator.start();
    }

    //TODO Might be good to refactor into a util class if needed elsewhere as well.
    /**
     * Formats the given time in milliseconds into a formatted text string.
     * The format is Hours:Minutes:Seconds:Milliseconds.
     * @param time The time to be parsed in milliseconds.
     * @return A formatted text string on the form H*:MM:SS.
     */
    private String parseTime(long time) {
        StringBuilder text = strBuilder;
        text.setLength(0);
        int h = 0;
        int m = 0;
        int s = 0;

        if (time >= DateUtils.HOUR_IN_MILLIS) {
            h = (int) (time/DateUtils.HOUR_IN_MILLIS);
            time -= h*DateUtils.HOUR_IN_MILLIS;
        }
        if (time >= DateUtils.MINUTE_IN_MILLIS) {
            m = (int) (time/DateUtils.MINUTE_IN_MILLIS);
            time -= m*DateUtils.MINUTE_IN_MILLIS;
        }
        if (time >= DateUtils.SECOND_IN_MILLIS) {
            s = (int) (time/DateUtils.SECOND_IN_MILLIS);
        }

        if (h>0) {
            text.append(h).append(":");
        }
        if (m<10) {
            text.append(0).append(m).append(":");
        } else {
            text.append(m).append(":");
        }
        if (s<10) {
            text.append(0).append(s);
        } else {
            text.append(s);
        }

        return text.toString();
    }

    //TODO Might be good to refactor into a util class if needed elsewhere as well.
    /**
     * Formats the given time in milliseconds into a formatted text string including milliseconds.
     * The format is Hours:Minutes:Seconds:Milliseconds.
     * @param time The time to be parsed in milliseconds.
     * @return A formatted text string on the form H*:MM:SS:mmm.
     */
    private String parseTimeDetailed(long time) {
        return parseTime(time) + (time % DateUtils.SECOND_IN_MILLIS);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}