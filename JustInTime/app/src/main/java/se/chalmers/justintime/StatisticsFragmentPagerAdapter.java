package se.chalmers.justintime;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import se.chalmers.justintime.fragments.ListStatisticsFragment;
import se.chalmers.justintime.fragments.TempStatisticsFragment;
import se.chalmers.justintime.fragments.WeekDetailedStatisticsFragment;

/**
 * Handles the tabs in the statistics view.
 * Created by Patrik on 2017-04-11.
 */

public class StatisticsFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[3];
    private Context context;

    public StatisticsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles[0] = context.getResources().getString(R.string.week);
        tabTitles[1] = context.getResources().getString(R.string.month);
        tabTitles[2] = context.getResources().getString(R.string.all_time);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 2) {
            return ListStatisticsFragment.newInstance();
        } else if (position == 0) {
            return WeekDetailedStatisticsFragment.newInstance(tabTitles[position]);
        } else {
            return TempStatisticsFragment.newInstance(tabTitles[position]);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
