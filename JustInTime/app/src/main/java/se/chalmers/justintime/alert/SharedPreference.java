package se.chalmers.justintime.alert;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by ABEL ASEFA on 4/13/2017.
 */

public class SharedPreference {
    private static final String TIME_TO_GO_ID = "TIME_TO_GO_ID";
    private static final String APP_USAGE = "APP_USAGE";
    private SharedPreferences mPreferences;

    public SharedPreference(Context c) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }
    public void setTimeToGo(long started) {
        //Log.d(TAG, "onReceive: setTimeToGO : " + started);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(TIME_TO_GO_ID, started);
        editor.apply();
    }
    public long getTimeToGo() {
        return mPreferences.getLong(TIME_TO_GO_ID, 0);
    }

    public void setAppUsageCount(long count) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(APP_USAGE, count);
        editor.apply();
    }
    public long getAppUsageCount() {
        return mPreferences.getLong(APP_USAGE, 0);
    }
}
