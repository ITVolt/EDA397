package se.chalmers.justintime.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;

import se.chalmers.justintime.Presenter;
import se.chalmers.justintime.R;
import se.chalmers.justintime.alert.BackgroundAlarm;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.fragments.StatisticsFragment;
import se.chalmers.justintime.fragments.TimerFragment;
import se.chalmers.justintime.fragments.TimerSequenceFragment;
import se.chalmers.justintime.timer.TimerService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreference mPreferences;
    private BackgroundAlarm backgroundAlarm;
    private long timerLength = 0; // In seconds
    private long wakeUpTime;
    private long savedTime;

    private Presenter presenter;

    Messenger timerService;
    boolean isBound;
    class MessagingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TimerService.ECHO:
                    Log.d("Messaging", "Received echo");
                    break;
                case TimerService.UPDATE_TIMER:
                    presenter.updateTimer((Long)msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidThreeTen.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPreferences = new SharedPreference(this);
        backgroundAlarm = new BackgroundAlarm(this);
        doBindService();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TimerFragment timerFragment = TimerFragment.newInstance();
        presenter = new Presenter(timerFragment);
        timerFragment.setPresenter(presenter);
        jumpToFragment(timerFragment);
    }
    final Messenger receiver = new Messenger(new MessagingHandler());
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = new Messenger(service);
            Log.d("Messaging", "Attached");
            try{
                Message message = Message.obtain(null, TimerService.REGISTER_CLIENT);
                message.replyTo = receiver;
                timerService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            presenter.setTimerService(timerService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
            presenter.setTimerService(null);
            Log.d("Messaging", "Disconnected");
        }
    };

    void doBindService(){
        bindService(new Intent(this, TimerService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
        Log.d("Messaging", "Binding ");
    }
    void doUnbindService(){
        if(isBound){
            unbindService(connection);
            isBound = false;
            Log.d("Messaging", "Unbinding");
        }
    }

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
        if (id == R.id.nav_timerA) {
            jumpToFragment(TimerFragment.newInstance());
        } else if (id == R.id.nav_timerB) {
            jumpToFragment(TimerSequenceFragment.newInstance());
        } else if (id == R.id.nav_timerC) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_statistics) {
            jumpToFragment(StatisticsFragment.newInstance());
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void jumpToFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.relativeLayoutForFragment, fragment, fragment.getTag())
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();

        Message message = Message.obtain(null, TimerService.ENTER_FOREGROUND);
        try {
            timerService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        if(TimerFragment.isTimmerRunning) {
            timerLength = mPreferences.getTimeToGo();
            wakeUpTime = (Calendar.getInstance().getTimeInMillis() + timerLength * 1000);
            backgroundAlarm.setAlalrm(wakeUpTime);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(timerService != null){
            Message message = Message.obtain(null, TimerService.LEAVE_FORGROUND);
            try {
                timerService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if(TimerFragment.isTimmerRunning) {
            backgroundAlarm.removeAlarm();
            if(wakeUpTime <= Calendar.getInstance().getTimeInMillis()) savedTime = 0;
            else savedTime = wakeUpTime - Calendar.getInstance().getTimeInMillis();
            TimerFragment.currentTimerValue = savedTime;
        }
    }

}