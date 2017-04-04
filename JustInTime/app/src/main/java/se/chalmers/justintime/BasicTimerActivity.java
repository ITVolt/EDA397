package se.chalmers.justintime;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

/**
 * This activity shows a basic timer counting down from a time to zero.
 * Created by Patrik on 2017-04-01.
 */

public class BasicTimerActivity extends AppCompatActivity
        implements CounterActivity{

    private long startValue;
    private long currentTimerValue;

    private boolean isTimerRunning;

    // private BasicTimer timer;    FIXME For when the real chronometer is implemented.
    private TextView timerText;
    private Chronometer chronometer; // FIXME Remove when the real chronometer is implemented.
    private String tempString = "Temp stuff, ignore"; // FIXME Remove when the real chronometer is implemented.

    private Button startPauseTimerButton;
    private Button resetTimerButton;

    private StringBuilder strBuilder = new StringBuilder(8);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_timer);



        timerText = (TextView) findViewById(R.id.basicTimerTV);
        chronometer = (Chronometer) findViewById(R.id.chronometerBasicTimer);

        startPauseTimerButton = (Button) findViewById(R.id.timerStartPauseButton);
        resetTimerButton = (Button) findViewById(R.id.timerResetButton);

        // FIXME Remove when the real chronometer is implemented.
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (isTimerRunning) {
                    currentTimerValue -= 1000;
                    updateTimerText();
                }
            }
        });
        setButtonOnClickListeners();

        startValue = 90000;
        reset();

        // Navigation drawer.
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */
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
    }

    @Override
    public void reset() {
        setRunningState(false);
        disableResetButton();
        currentTimerValue = startValue; // FIXME Remove when the real chronometer is implemented.
        updateTimerText();
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
        //TODO Something nice when the timer is finished.
    }

    private void setRunningState(boolean run) {
        if (run) {
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
        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.fade_in);
        animator.setTarget(resetTimerButton);
        animator.start();
    }

    private void disableResetButton() {
        resetTimerButton.setEnabled(false);
        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.fade_out);
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

        if (time > DateUtils.HOUR_IN_MILLIS) {
            h = (int) (time/DateUtils.HOUR_IN_MILLIS);
            time -= h*DateUtils.HOUR_IN_MILLIS;
        }
        if (time > DateUtils.MINUTE_IN_MILLIS) {
            m = (int) (time/DateUtils.MINUTE_IN_MILLIS);
            time -= m*DateUtils.MINUTE_IN_MILLIS;
        }
        if (time > DateUtils.SECOND_IN_MILLIS) {
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

    // Navigation drawer stuff.
    /*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    */
}
