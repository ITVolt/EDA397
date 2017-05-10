package se.chalmers.justintime.fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.chalmers.justintime.Presenter;
import se.chalmers.justintime.R;
import se.chalmers.justintime.activities.CounterActivity;
import se.chalmers.justintime.alert.Alarm;
import se.chalmers.justintime.alert.AlarmBuilder;

/**
 * This fragment shows a basic timer counting down from a time to zero.
 * Created by Patrik on 2017-04-01.
 */

public class TimerFragment extends Fragment implements CounterActivity {

    private long startValue;
    private long currentTimerValue;

    private String label;
    private String[] tags;

    private boolean isTimerRunning = false;

    private EditText timerText;
    private TextView timerLabel;
    private TextView timerTagText;
    private TextView timerTagList;

    private Button startPauseTimerButton;

    private ImageButton playPauseButton;
    private ImageButton resetButton;

    private Alarm alarm;
    private StringBuilder strBuilder = new StringBuilder(8);

    private View view;

    private Presenter presenter;

    private ProgressBar progressBarCircle;

    private int timerId;

    public Map<Integer,String> map = new HashMap<>();
    private TextView previewNextTimerText;
    private ImageButton nextTimerButton;
    private boolean isSequential;


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
    public static TimerFragment newInstance(boolean isSequential) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putBoolean("isSequential",isSequential);
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

        isSequential = getArguments().getBoolean("isSequential");

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
                    if (newTime == 0) {
                        startPauseTimerButton.setEnabled(false);
                    }else {
                        startPauseTimerButton.setEnabled(true);
                    }
                    presenter.removeTimer(timerId);
                    ArrayList<Long> durations = new ArrayList<Long>(2);
                    durations.add(newTime);
                    if(isSequential){
                        durations.add(newTime * 2);
                        nextTimerButton.setVisibility(View.VISIBLE);
                        previewNextTimerText.setVisibility(View.VISIBLE);
                    }
                    timerId = presenter.newTimer(label,tags,durations);
                    startValue = newTime;
                    currentTimerValue = newTime;
                    updateTimerText();
                    setProgressBarValues();
                    previewNextTimerText.setText(parseTime( startValue * 2));
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(timerText.getWindowToken(), 0);
                    return true;

                }
                return false;

            }
        });

        this.label = "Basic timer";
        this.tags = new String[0];

        playPauseButton = (ImageButton) view.findViewById(R.id.imageButton);
        resetButton = (ImageButton) view.findViewById(R.id.imageButton2);
        timerText = (EditText) view.findViewById(R.id.basicTimerTV);
        startPauseTimerButton = (Button) view.findViewById(R.id.timerStartPauseButton);
        timerLabel = (TextView) view.findViewById(R.id.timerLabelTV);
        timerTagText = (TextView) view.findViewById(R.id.timerTagLabelTV);
        timerTagList = (TextView) view.findViewById(R.id.timerTagsTV);
        previewNextTimerText = (TextView) view.findViewById(R.id.previewTV);
        nextTimerButton = (ImageButton) view.findViewById(R.id.nextButton);

        timerLabel.setText(label);
        updateTagListText();

        setRunningState(isTimerRunning);

        AlarmBuilder ab = new AlarmBuilder(view.getContext());
        ab.setUseSound(true);
        ab.setUseVibration(true);
        alarm = ab.getAlarmInstance();

        if (!isSequential) {
            nextTimerButton.setVisibility(View.INVISIBLE);
            previewNextTimerText.setVisibility(View.INVISIBLE);
        }
        previewNextTimerText.setText("");//Set it invisible since no initial time set

        setButtonOnClickListeners();
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

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) startValue);
        progressBarCircle.setProgress((int) (currentTimerValue) );
    }

    @Override
    public void start() {
        setRunningState(true);
        presenter.startTimer(timerId);
    }

    @Override
    public void pause() {
        presenter.pauseTimer(timerId);
        setRunningState(false);
    }

    @Override
    public void reset() {
        if (!isTimerRunning) {
            enableStartPauseTimerButton();
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        }
        setRunningState(false);
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();
        progressBarCircle.setProgress((int) startValue);
        presenter.resetTimer(timerId);
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

    private void setNewTags(String[] newTags) {
        if (!Arrays.equals(tags, newTags)) {
            presenter.setTimerTags(timerId, newTags);
            tags = newTags;
            updateTagListText();
        }
    }

    private void updateTagListText() {
        StringBuilder tagText = new StringBuilder();
        if (tags.length > 0) {
            tagText.append(tags[0]);
            if (tags.length > 1) {
                tagText.append("\n").append(tags[1]);
                if (tags.length > 2) {
                    tagText.append("\n+").append(tags.length-2);
                }
            }
        } else {
            tagText.append("<no tags set>");
        }
        timerTagList.setText(tagText.toString());
    }


    public void onTimerFinish() {
        Log.d("TimerFragment", "onTimerFinish: Time's up!");
        alarm.alert();
        if (isSequential) {
            nextTimerButton.setVisibility(View.INVISIBLE);
            previewNextTimerText.setVisibility(View.INVISIBLE);
            startValue = startValue * 2;
            setProgressBarValues();
        }else{
            startPauseTimerButton.setText(R.string.timer_button_stop);
            disableStartPauseTimerButton();
            setRunningState(false);
        }
    }

    private void setRunningState(boolean run) {
        if (run) {
            startPauseTimerButton.setText(R.string.timer_button_pause);
            disableResetButton();
            isTimerRunning = true;
        } else {
            startPauseTimerButton.setText(R.string.timer_button_start);
            enableResetButton();
            isTimerRunning = false;
        }
    }

    private void timerLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Enter new label");

        // Set up the input
        final EditText input = new EditText(view.getContext());
        input.setText(label);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                label = input.getText().toString().trim();
                presenter.updateTimerLabel(timerId, label);
                timerLabel.setText(label);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void timerTagDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final boolean hasTags;
        builder.setTitle("Tags");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1);
        if (tags.length == 0) {
            hasTags = false;
            arrayAdapter.add("<No tags set>");
        } else {
            hasTags = true;
            arrayAdapter.addAll(tags);
        }

        builder.setAdapter(arrayAdapter, null);

        if (hasTags) {
            builder.setNegativeButton("Remove tag", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeTagDialog();
                }
            });
        }

        builder.setNeutralButton("Add tag", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addTagDialog();
            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void removeTagDialog() {
        final AlertDialog.Builder builderInner = new AlertDialog.Builder(view.getContext());
        builderInner.setTitle("Select to remove");
        final boolean[] checked = new boolean[tags.length];

        builderInner.setMultiChoiceItems(tags, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked[which] = isChecked;
            }
        });

        builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderInner.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> remaining = new ArrayList<>();
                for (int i = 0; i < tags.length; i++) {
                    if (!checked[i]) {
                        remaining.add(tags[i]);
                    }
                }
                setNewTags(remaining.toArray(new String[remaining.size()]));
            }
        });
        builderInner.show();
    }

    private void addTagDialog() {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(view.getContext());
        builderInner.setTitle("Enter new tag");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSelection(0);
        builderInner.setView(input);

        builderInner.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] newTags = Arrays.copyOf(tags, tags.length+1);
                newTags[newTags.length-1] = input.getText().toString().trim();
                setNewTags(newTags);
            }
        });
        builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builderInner.show();
    }

    private void setButtonOnClickListeners() {
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long time = convertStringToMilliseconds(timerText.getText().toString());

                if (isTimerRunning) {
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    pause();
                } else if (time != 0) {
                    start();
                    playPauseButton.setImageResource(R.drawable.ic_pause_black_48dp);
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        timerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerLabelDialog();
            }
        });
        timerTagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerTagDialog();
            }
        });
        timerTagList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerTagDialog();
            }
        });
        nextTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long newTime = startValue + 60*1000;
                presenter.removeTimer(timerId);
                ArrayList<Long> durations = new ArrayList<Long>(1);
                durations.add(newTime);
                timerId = presenter.newTimer(label,tags,durations);
                startValue = newTime;
                currentTimerValue = newTime;
                updateTimerText();
                setProgressBarValues();
                previewNextTimerText.setText(parseTime( startValue + (60*1000)));
            }
        });
    }

    private void enableResetButton() {
        resetButton.setEnabled(true);
        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.fade_in);
        animator.setTarget(resetButton);
        animator.start();
    }

    private void disableResetButton() {
        resetButton.setEnabled(false);
        Animator animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.fade_out);
        animator.setTarget(resetButton);
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
        presenter.startSendingUpdates(timerId);
        this.isTimerRunning = presenter.getRunningState(timerId);
    }

    public void setTimerId(int timerId) {
        this.timerId = timerId;
    }
}