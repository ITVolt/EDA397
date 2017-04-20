package se.chalmers.justintime.fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

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
    //private long currentTimerValue;
    public static long currentTimerValue;
    private long previousDuration;
    private LocalDateTime startTime;

    private boolean isTimerRunning;
    private SharedPreference mPreferences;

    // private BasicTimer timer;    FIXME For when the real chronometer is implemented.
    private TextView timerText;
    private Chronometer chronometer; // FIXME Remove when the real chronometer is implemented.
    private String tempString = "Temp stuff, ignore"; // FIXME Remove when the real chronometer is implemented.

    private Button startPauseTimerButton;
    private Button resetTimerButton;

    private Alarm alarm;
    private StringBuilder strBuilder = new StringBuilder(8);

    private DatabaseHelper databaseHelper;
    private int currentPauseId;

    private View view;
    public static boolean isTimmerRunning = false;


    private Presenter presenter;

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
        chronometer = (Chronometer) view.findViewById(R.id.chronometerBasicTimer);

        startPauseTimerButton = (Button) view.findViewById(R.id.timerStartPauseButton);
        resetTimerButton = (Button) view.findViewById(R.id.timerResetButton);

        // Set up the alarm.
        mPreferences = new SharedPreference(view.getContext());
        AlarmBuilder ab = new AlarmBuilder(view.getContext());
        ab.setUseSound(true);
        ab.setUseVibration(true);
        alarm = ab.getAlarmInstance();

        // FIXME Remove when the real chronometer is implemented.
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (isTimerRunning) {
                    currentTimerValue -= 1000;
                    mPreferences.setTimeToGo(currentTimerValue/1000);
                    updateTimerText();
                    if (currentTimerValue <= 0) {
                        onTimerFinish();
                    }
                }
            }
        });
        setButtonOnClickListeners();
        databaseHelper = new DatabaseHelper(this.getContext());

        startValue = 90000;
        reset();
        return view;
    }


    public void setTimerStartValue (long startValue) {
        this.startValue = startValue;
        setRunningState(false);
    }

    @Override
    public void start() {
        setRunningState(true);
    }

    @Override
    public void pause() {
        setRunningState(false);
        long duration = startValue - currentTimerValue - previousDuration;
        previousDuration = previousDuration + duration;
        TimerLogEntry entry = new TimerLogEntry(databaseHelper.getNextAvailableId(), currentPauseId,startTime, duration);
        databaseHelper.insertTimer(entry);
    }

    @Override
    public void reset() {
        setRunningState(false);
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();
        previousDuration = 0;
        currentPauseId = databaseHelper.getNextAvailablePauseId();
    }

    @Override
    public void updateTime(long ms) {
        //currentTimerValue = ms;   FIXME For when the real chronometer is implemented.
        updateTimerText();
        if (currentTimerValue <= 0) {
            onTimerFinish();
        }
    }

    private void updateTimerText() {
        timerText.setText(parseTime(currentTimerValue));
        chronometer.setText(tempString);
    }

    private void onTimerFinish() {
        Log.d("TimerFragment", "onTimerFinish: Time's up!");
        alarm.alert();
        setRunningState(false);
        long duration = startValue - currentTimerValue - previousDuration;
        TimerLogEntry entry = new TimerLogEntry(databaseHelper.getNextAvailableId(), currentPauseId, startTime, duration);
        databaseHelper.insertTimer(entry);
    }

    private void setRunningState(boolean run) {
        if (run) {
            startTime = LocalDateTime.now();
            startPauseTimerButton.setText(R.string.timer_button_pause);
            disableResetButton();
            isTimerRunning = true;
            chronometer.start();  //FIXME Remove when the real chronometer is implemented.
        } else {
            startPauseTimerButton.setText(R.string.timer_button_start);
            enableResetButton();
            isTimerRunning = false;
            chronometer.stop();  //FIXME Remove when the real chronometer is implemented.
        }
        isTimmerRunning = isTimerRunning;
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