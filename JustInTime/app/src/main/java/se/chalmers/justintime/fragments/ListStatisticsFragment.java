package se.chalmers.justintime.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.justintime.R;
import se.chalmers.justintime.StatisticsBundle;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.TagListAdapter;

import static android.content.ContentValues.TAG;

public class ListStatisticsFragment extends Fragment {

    Boolean isClickedGeneral = true, isClickedTag = false;
    View view;
    ListView tagListView ;
    Button tagInfo, generalInfo;
    TextView timerInfoText, appInfoText;
    PieChart tagPieChart;
    SharedPreference mPreferences;
    DatabaseHelper db;
    Map<String, Boolean> tagsToShow = new HashMap<>();
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
        tagListView = (ListView) view.findViewById(R.id.tagList);
        tagInfo.getBackground().setAlpha(20);
        generalInfo.getBackground().setAlpha(20);
        tagPieChart.setUsePercentValues(true); //Show the values in percent
        tagPieChart.setCenterText("Time per Tag");
        timerInfoText.setText("Used for : " + db.getTotalDuration());
        appInfoText.setText("App Used : " + mPreferences.getAppUsageCount() + " times");
        tagPieChart.setVisibility(View.GONE);
        tagListView.setVisibility(View.GONE);
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
                    tagListView.setVisibility(View.VISIBLE);
                    showPieChart(false);
                    showTagListView();
                    isClickedTag = true;

                } else {
                    tagPieChart.setVisibility(View.GONE);
                    tagListView.setVisibility(View.GONE);
                    isClickedTag = false;
                }
            }
        });
    }
    private void showTagListView() {

        String[] tags = db.getTags();
        Map<String, Long> totalTimePerTag;
        totalTimePerTag = getTotalTimePerTag(tags);
        List<Map.Entry<String, Long>> totalTimePerTagEntrySet = new ArrayList<Map.Entry<String, Long>>(
                totalTimePerTag.entrySet());
        TagListAdapter adapter = new TagListAdapter(view.getContext(), totalTimePerTagEntrySet);

        // Assign adapter to ListView
        tagListView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(tagListView);
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean keep = !tagsToShow.get(((Map.Entry<String, Long>) tagListView.getItemAtPosition(position)).getKey());
                tagsToShow.put(((Map.Entry<String, Long>) tagListView.getItemAtPosition(position)).getKey(), keep);
                showPieChart(true);

            }
        });
    }


    private void showPieChart(Boolean update) {

        String[] tags = db.getTags();
        Map<String, Long> totalTimePerTag;
        totalTimePerTag = getTotalTimePerTag(tags);
        List<PieEntry> entries = new ArrayList<>();
        List<Map.Entry<String, Long>> totalTimePerTagEntrySet = new ArrayList<Map.Entry<String, Long>>(
                totalTimePerTag.entrySet());
        if (!update) {
            for (String tag : tags) {
                tagsToShow.put(tag, false);
            }
            selectTopFive(totalTimePerTag);
        }

        for (Map.Entry<String, Long> entry : totalTimePerTagEntrySet) {
            if (tagsToShow.get(entry.getKey())) {
                String tag = entry.getKey();
                Long value = entry.getValue();
                Log.d("Check : ", tag + " : " + value);
                entries.add(new PieEntry(value, tag));
            }
        }

        PieDataSet set = new PieDataSet(entries, "Time per Tag");
        PieData data = new PieData(set);
        tagPieChart.setNoDataText("No Data");
        tagPieChart.setData(data);
        tagPieChart.animateY(5000);
        tagPieChart.getLegend().setEnabled(false);
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        data.setValueTextSize(20f);
        //data.setValueTextColor(getResources().getColor(R.color.colorAccent));
        set.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
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

    private void selectTopFive(Map<String, Long> map) {
        List<Map.Entry<String, Long>> topFive;

        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(
                map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {

            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                if (o1.getValue() < o2.getValue()) {
                    return 1;
                } else if (o1.getValue() > o2.getValue()) {
                    return -1;
                }
                return 0;
            }
        });
        Log.d(TAG, "getTopFive: " + list.size());
        topFive = list.subList(0, 5);
        for (Map.Entry<String, Long> tag : topFive) {
            tagsToShow.put(tag.getKey(), true);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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
