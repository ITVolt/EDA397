package se.chalmers.justintime.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import se.chalmers.justintime.R;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.database.DatabaseHelper;

import static android.content.ContentValues.TAG;

public class ListStatisticsFragment extends Fragment {

    Boolean isClickedGeneral = false, isClickedTag = false;
    View view;
    Button tagInfo, generalInfo;
    TextView timerInfoText, appInfoText;
    PieChart tagPieChart;
    SharedPreference mPreferences;
    DatabaseHelper db;
    private OnFragmentInteractionListener mListener;

    public ListStatisticsFragment() {
        // Required empty public constructor
    }


    public static ListStatisticsFragment newInstance() {
        ListStatisticsFragment fragment = new ListStatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = new SharedPreference(getContext());
        db = DatabaseHelper.getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_statistics, container, false);
        tagInfo = (Button) view.findViewById(R.id.tagInfoSwitch);
        generalInfo = (Button) view.findViewById(R.id.generalInfoSwitch);
        timerInfoText = (TextView) view.findViewById(R.id.timerInfoText);
        appInfoText = (TextView) view.findViewById(R.id.appInfoText);
        tagPieChart = (PieChart) view.findViewById(R.id.tagPieChart);
        timerInfoText.setVisibility(View.GONE);
        appInfoText.setVisibility(View.GONE);
        tagPieChart.setVisibility(View.GONE);
        setOnSwitchChangeListener();
        return view;
    }

    private void setOnSwitchChangeListener() {

        generalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickedGeneral) {
                    String totalDuration = db.getTotalDuration();
                    if (totalDuration == null) {
                        totalDuration = "0 Hour";
                    }
                    timerInfoText.setText("Used for : " + totalDuration);
                    appInfoText.setText("Used : " + mPreferences.getAppUsageCount() + " times");
                    timerInfoText.setVisibility(View.VISIBLE);
                    appInfoText.setVisibility(View.VISIBLE);
                    isClickedGeneral = true;
                } else {
                    timerInfoText.setVisibility(View.GONE);
                    appInfoText.setVisibility(View.GONE);
                    isClickedGeneral = false;
                }
            }
        });
        tagInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickedTag) {
                    tagPieChart.setVisibility(View.VISIBLE);
                    showPieChart();
                    isClickedTag = true;
                } else {
                    tagPieChart.setVisibility(View.GONE);
                    isClickedTag = false;
                }
            }
        });
    }

    private void showPieChart() {

        String[] tags = db.getTags();
        Map<String, Long> totalTimePerTag;
        totalTimePerTag = getTotalTimePerTag(tags);

        List<PieEntry> entries = new ArrayList<>();

        //Get the top Five
        if (tags.length > 5) {
            for (Map.Entry<String, Long> entry : getTopFive(totalTimePerTag))
            {
                String tag = entry.getKey();
                Long value = entry.getValue();
                Log.d("Check : " , tag + " : " + value);
                entries.add(new PieEntry(value, tag));

            }
        }
        else {
            for (String tag : tags) {
                entries.add(new PieEntry(db.getTagTime(tag), tag));
            }
        }
        PieDataSet set = new PieDataSet(entries, "Time per Tag");
        PieData data = new PieData(set);
        tagPieChart.setData(data);
        data.setValueTextSize(20f);
        data.setValueTextColor(R.color.secondary_text);
        set.setValueTextColor(R.color.secondary_text);
        tagPieChart.invalidate(); // refresh
    }

    private Map<String, Long> getTotalTimePerTag(String[] tags) {
        Map<String, Long> myMap = new HashMap<String, Long>();
        for (int i = 0; i < tags.length; i++) {
            myMap.put(tags[i], db.getTagTime(tags[i]));
        }
        return myMap;
    }

    /*
    *  This Method Returns the top Five
     */

    private List<Map.Entry<String, Long>> getTopFive(Map<String, Long> map) {
        List<Map.Entry<String, Long>> topFive;
        Set<Map.Entry<String, Long>> set = map.entrySet();

        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(
                set);

        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {

            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                int i = 0;
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                } else if (o1.getValue() > o2.getValue()) {
                    return -1;
                }
                return i;
            }});
        Log.d(TAG, "getTopFive: " + list.size());
        topFive = list.subList(0, 5);
        return  topFive;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
