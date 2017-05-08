package se.chalmers.justintime.fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.threeten.bp.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private EditText timerText;

    private Button startPauseTimerButton;
    private Button resetTimerButton;

    private Alarm alarm;
    private StringBuilder strBuilder = new StringBuilder(8);

    private DatabaseHelper databaseHelper;
    private int currentPauseId;

    private View view;


    private Presenter presenter;

    private long timeCountInMilliSeconds;

    private ProgressBar progressBarCircle;
    private int timerId;
    private int timerFragmentId;

    public Map<Integer,String> map = new HashMap<>();



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


        timerText = (EditText) view.findViewById(R.id.basicTimerTV);
        timerText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timerText.setSelection(timerText.getText().length());
            }
        });

        timerText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    timerText.setText(removeNumberFromTimerText());
                    return true;

                } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode != KeyEvent.KEYCODE_DEL && keyCode != KeyEvent.KEYCODE_ENTER) {
                    timerText.setText(addNumberToTimerText(keyCode-7));
                    timerText.setSelection(timerText.getText().length());
                    return true;

                } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    long newTime = convertStringToMilliseconds(timerText.getText().toString());
                    startValue = newTime;
                    currentTimerValue = newTime;
                    timeCountInMilliSeconds  = newTime;
                    updateTimerText();
                    setProgressBarValues();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(timerText.getWindowToken(), 0);
                    return true;

                }
                return false;

            }
        });

        startPauseTimerButton = (Button) view.findViewById(R.id.timerStartPauseButton);
        resetTimerButton = (Button) view.findViewById(R.id.timerResetButton);
        setRunningState(isTimerRunning);
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
        disableResetButton();
        return view;
    }

    public String removeNumberFromTimerText() {
        addTimerTextToMap();

        map.put(1, map.get(2));
        map.put(2, map.get(3));
        map.put(3, map.get(4));
        map.put(4, map.get(5));
        map.put(5, map.get(6));
        map.put(6, "0");

        return map.get(6) + map.get(5) + ":" + map.get(4) + map.get(3) + ":" + map.get(2) + map.get(1);
    }

    private void addTimerTextToMap() {
        String currentTimerText = timerText.getText().toString();
        Character[] c = convertStringToArray(currentTimerText);

        map.put(6, c[0].toString());
        map.put(5, c[1].toString());
        map.put(4, c[3].toString());
        map.put(3, c[4].toString());
        map.put(2, c[6].toString());
        map.put(1, c[7].toString());
    }

    public String addNumberToTimerText(int keyPressed) {
        addTimerTextToMap();

        map.put(6, map.get(5));
        map.put(5, map.get(4));
        map.put(4, map.get(3));
        map.put(3, map.get(2));
        map.put(2, map.get(1));
        map.put(1, String.valueOf(keyPressed));

        return map.get(6) + map.get(5) + ":" + map.get(4) + map.get(3) + ":" + map.get(2) + map.get(1);
    }

    // Input must be HH:MM:SS
    private long convertStringToMilliseconds(String textToConvert) {

        Pattern p = Pattern.compile("(\\d+):(\\d+):(\\d+)");
        Matcher m = p.matcher(textToConvert);
        if (m.matches()) {
            int hrs = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            int sec = Integer.parseInt(m.group(3));
            long ms = (long) hrs * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000;
            System.out.println("hrs="+hrs+ " min="+min+" sec="+sec+" ms="+ms);
            return ms;
        }
        return 0;
    }

    public Character[] convertStringToArray(String s ) {
        if ( s == null ) {
            return null;
        }

        int len = s.length();
        Character[] array = new Character[len];
        for (int i = 0; i < len ; i++) {
            array[i] = Character.valueOf(s.charAt(i));

        }
        return array;

    }

    private void resetCountDownTimer() {
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
        presenter.startTimer(timerFragmentId);
    }

    @Override
    public void pause() {
        presenter.pauseTimer(timerFragmentId);
        setRunningState(false);
        long duration = startValue - currentTimerValue - previousDuration;
        previousDuration = previousDuration + duration;
        TimerLogEntry entry = new TimerLogEntry(timerId, startTime, duration);
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
        presenter.resetTimer(timerFragmentId);
    }

    @Override
    public void updateTime(long ms) {
        //Log.d("UPDATE TIME", ms + " ");
        currentTimerValue = ms;
        updateTimerText();
        if (progressBarCircle != null) {
            progressBarCircle.setProgress((int) (ms));
        }
    }

    private void updateTimerText() {
        if (timerText != null) {
            timerText.setText(parseTime(currentTimerValue));
        }
    }

    public void onTimerFinish() {
        Log.d("TimerFragment", "onTimerFinish: Time's up!");
        alarm.alert();
        startPauseTimerButton.setText(R.string.timer_button_stop);
        disableStartPauseTimerButton();
        presenter.pauseTimer(timerFragmentId);
        setRunningState(false);
        long duration = startValue - currentTimerValue - previousDuration;
        TimerLogEntry entry = new TimerLogEntry(timerId, startTime, duration);
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

        if (h<=9) {
            text.append(0);
        }
        text.append(h).append(":");

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
        presenter.startSendingUpdates(timerFragmentId);
        this.isTimerRunning = presenter.getRunningState(timerFragmentId);
    }

    public void setTimerFragmentId(int timerFragmentId) {
        this.timerFragmentId = timerFragmentId;
    }
}