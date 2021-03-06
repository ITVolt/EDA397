package se.chalmers.justintime.fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static long currentTimerValue;

    private String label;
    private String[] tags;

    private boolean isTimerRunning = false;

    private TextView timerText;
    private TextView timerLabel;
    private TextView timerTagText;
    private TextView timerTagList;

    private Button startPauseTimerButton;
    private Button resetTimerButton;

    private ImageButton playPausButton;
    private ImageButton resetButton;

    private Alarm alarm;
    private StringBuilder strBuilder = new StringBuilder(8);

    private View view;

    private Presenter presenter;

    private long timeCountInMilliSeconds;

    private ProgressBar progressBarCircle;

    private int timerId;


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

        this.label = "Basic timer";
        this.tags = new String[0];

        playPausButton = (ImageButton) view.findViewById(R.id.imageButton);
        resetButton = (ImageButton) view.findViewById(R.id.imageButton2);
        timerText = (TextView) view.findViewById(R.id.basicTimerTV);
        startPauseTimerButton = (Button) view.findViewById(R.id.timerStartPauseButton);
        resetTimerButton = (Button) view.findViewById(R.id.timerResetButton);
        timerLabel = (TextView) view.findViewById(R.id.timerLabelTV);
        timerTagText = (TextView) view.findViewById(R.id.timerTagLabelTV);
        timerTagList = (TextView) view.findViewById(R.id.timerTagsTV);

        timerLabel.setText(label);
        updateTagListText();

        setRunningState(isTimerRunning);
        // Set up the alarm.

        AlarmBuilder ab = new AlarmBuilder(view.getContext());
        ab.setUseSound(true);
        ab.setUseVibration(true);
        alarm = ab.getAlarmInstance();

        setButtonOnClickListeners();
        progressBarCircle = (ProgressBar) view.findViewById(R.id.progressBarCircle);
        startValue = 90000;
        timeCountInMilliSeconds = startValue;
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();
        setProgressBarValues();
        return view;
    }

    private void resetCountDownTimer() {
        resetProgressBarValues();
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) startValue);
        progressBarCircle.setProgress((int) (timeCountInMilliSeconds) );
    }

    private void resetProgressBarValues() {
        progressBarCircle.setProgress((int) startValue);
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
        setRunningState(false);
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();
        resetCountDownTimer();
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
        startPauseTimerButton.setText(R.string.timer_button_stop);
        setRunningState(false);
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
        playPausButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    playPausButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    pause();
                } else {
                    start();
                    playPausButton.setImageResource(R.drawable.ic_pause_black_48dp);
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
        presenter.startSendingUpdates(timerId);
        this.isTimerRunning = presenter.getRunningState(timerId);
    }

    public void setTimerId(int timerId) {
        this.timerId = timerId;
    }
}