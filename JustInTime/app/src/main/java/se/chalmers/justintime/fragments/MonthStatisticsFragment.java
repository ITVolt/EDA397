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

import org.threeten.bp.LocalDateTime;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import se.chalmers.justintime.MonthListAdapter;
import se.chalmers.justintime.R;
import se.chalmers.justintime.StatisticsBundle;
import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.database.TimerInfoBundle;

/**
 * A fragment representing a list of Items.
 * Created by Patrik on 2017-04-24.
 */
public class MonthStatisticsFragment extends Fragment {

    private StatisticsBundle[] monthData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MonthStatisticsFragment() {
    }

    public static MonthStatisticsFragment newInstance() {
        MonthStatisticsFragment fragment = new MonthStatisticsFragment();
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
        View view = inflater.inflate(R.layout.fragment_month, container, false);

        // Gather the monthly statistics.
        populateMonthData(DatabaseHelper.getInstance(this.getContext()).getAllTimerInfo());

        // TODO WeekDetailedStatisticsFragment is reused, create a new for month to use here instead.
        final ListView listview = (ListView) view.findViewById(R.id.month_list_view);
        listview.setAdapter(new MonthListAdapter(view.getContext(), monthData));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                MonthDetailedStatisticsFragment monthDetailedStatisticsFragment = MonthDetailedStatisticsFragment.newInstance();
                monthDetailedStatisticsFragment.setData((StatisticsBundle) listview.getItemAtPosition(position));
                manager.beginTransaction().replace(
                        R.id.relativeLayoutForFragment, monthDetailedStatisticsFragment)
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

    private void populateMonthData(TimerInfoBundle[] timerInfoBundles) {
        SparseArray<SparseArray<ArrayList<TimerInfoBundle>>> years = new SparseArray<>();
        DateFormatSymbols monthConverter = DateFormatSymbols.getInstance();
        LocalDateTime date;
        int monthNbr;
        int yearNbr;

        for (TimerInfoBundle tib : timerInfoBundles) {
            if (!tib.getTimes().isEmpty()) {
                date = tib.getTimes().get(0).first;
                monthNbr = date.getMonthValue();
                yearNbr = date.getYear();

                SparseArray<ArrayList<TimerInfoBundle>> year = years.get(yearNbr);
                if (year == null) {
                    years.append(yearNbr, new SparseArray<ArrayList<TimerInfoBundle>>());
                    year = years.get(yearNbr);
                }
                ArrayList<TimerInfoBundle> month = year.get(monthNbr);
                if (month == null) {
                    year.append(monthNbr, new ArrayList<TimerInfoBundle>());
                    month = year.get(monthNbr);
                }
                month.add(tib);
            }
        }

        ArrayList<StatisticsBundle> monthBundles = new ArrayList<>();

        for (int i = years.size()-1; i>=0; i--) {
            SparseArray<ArrayList<TimerInfoBundle>> year = years.valueAt(i);
            for (int j = year.size()-1; j>=0; j--) {
                ArrayList<TimerInfoBundle> month = year.valueAt(j);
                monthBundles.add(new StatisticsBundle(monthConverter.getMonths()[year.keyAt(j)-1],
                        month.toArray(new TimerInfoBundle[month.size()])));
            }
        }

        monthData = monthBundles.toArray(new StatisticsBundle[monthBundles.size()]);
    }
}
