package se.chalmers.justintime.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.WeekFields;

import java.util.ArrayList;
import java.util.Locale;

import se.chalmers.justintime.R;
import se.chalmers.justintime.StatisticsBundle;
import se.chalmers.justintime.WeekListAdapter;
import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.database.TimerInfoBundle;

/**
 * A fragment representing a list of Items.
 * Created by Patrik on 2017-04-24.
 */
public class WeekStatisticsFragment extends Fragment {

    private StatisticsBundle[] weekData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeekStatisticsFragment() {
    }

    public static WeekStatisticsFragment newInstance() {
        WeekStatisticsFragment fragment = new WeekStatisticsFragment();
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
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        // Gather the weekly statistics.
        populateWeekData(DatabaseHelper.getInstance(this.getContext()).getAllTimerInfo());

        final ListView listview = (ListView) view.findViewById(R.id.week_list_view);
        listview.setAdapter(new WeekListAdapter(view.getContext(), weekData));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                WeekDetailedStatisticsFragment weekDetailedStatisticsFragment = WeekDetailedStatisticsFragment.newInstance();
                weekDetailedStatisticsFragment.setData((StatisticsBundle) listview.getItemAtPosition(position));
                manager.beginTransaction().replace(
                        R.id.relativeLayoutForFragment, weekDetailedStatisticsFragment)
                        .commit();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void populateWeekData(TimerInfoBundle[] timerInfoBundles) {
        SparseArray<SparseArray<ArrayList<TimerInfoBundle>>> years = new SparseArray<>();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        LocalDateTime date;
        int weekNbr;
        int yearNbr;

        for (TimerInfoBundle tib : timerInfoBundles) {
            if (!tib.getTimes().isEmpty()) {
                date = tib.getTimes().get(0).first;
                weekNbr = date.get(woy);
                yearNbr = date.getYear();
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {  //Had to add this because it was getting the wrong week for sundays.
                    weekNbr--;
                }
                SparseArray<ArrayList<TimerInfoBundle>> year = years.get(yearNbr);
                if (year == null) {
                    years.append(yearNbr, new SparseArray<ArrayList<TimerInfoBundle>>());
                    year = years.get(yearNbr);
                }
                ArrayList<TimerInfoBundle> week = year.get(weekNbr);
                if (week == null) {
                    year.append(weekNbr, new ArrayList<TimerInfoBundle>());
                    week = year.get(weekNbr);
                }
                week.add(tib);
            }
        }

        ArrayList<StatisticsBundle> weekBundles = new ArrayList<>();

        for (int i = years.size()-1; i>=0; i--) {
            SparseArray<ArrayList<TimerInfoBundle>> year = years.valueAt(i);
            for (int j = year.size()-1; j>=0; j--) {
                ArrayList<TimerInfoBundle> week = year.valueAt(j);
                weekBundles.add(new StatisticsBundle(Integer.toString(year.keyAt(j)),
                        week.toArray(new TimerInfoBundle[week.size()])));
            }
        }

        weekData = weekBundles.toArray(new StatisticsBundle[weekBundles.size()]);
    }
}
