package se.chalmers.justintime.fragments;

import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import se.chalmers.justintime.DateFormatterUtil;
import se.chalmers.justintime.R;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.database.DatabaseHelper;
import se.chalmers.justintime.TagListAdapter;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;

public class ListStatisticsFragment extends Fragment {

    Boolean isClickedGeneral = true, isClickedTag = false, isClickedAllInfo = false;
    View view;
    ListView tagListView;
    Button tagInfo, generalInfo, allInfo;
    TextView timerInfoText, appInfoText, favoriteTagText;
    PieChart tagPieChart;
    TableLayout allInfoTable;
    TextView textView_0, textView_1, textView_2, textView2, textView02, textView03;
    TableRow row02, tableRow;
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
        allInfo = (Button) view.findViewById(R.id.allInfoSwitch);
        timerInfoText = (TextView) view.findViewById(R.id.timerInfoText);
        favoriteTagText = (TextView) view.findViewById(R.id.favoriteTageText);
        appInfoText = (TextView) view.findViewById(R.id.appInfoText);
        tagPieChart = (PieChart) view.findViewById(R.id.tagPieChart);
        tagListView = (ListView) view.findViewById(R.id.tagList);
        allInfoTable = (TableLayout) view.findViewById(R.id.table_main);
        tagInfo.getBackground().setAlpha(20);
        generalInfo.getBackground().setAlpha(20);
        allInfo.getBackground().setAlpha(20);
        tagPieChart.setUsePercentValues(true); //Show the values in percent
        tagPieChart.setCenterText("Time per Tag");
        timerInfoText.setText("Used for : " + db.getTotalDuration());
        appInfoText.setText("App Used : " + mPreferences.getAppUsageCount() + " times");
        favoriteTagText.setText("Favorite Tag : " + favoriteTag());
        tagPieChart.setVisibility(View.GONE);
        tagListView.setVisibility(View.GONE);
        allInfoTable.setVisibility(View.GONE);
        setOnSwitchChangeListener();
        showAllData(db);
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
                    favoriteTagText.setVisibility(View.VISIBLE);
                    isClickedGeneral = true;
                } else {
                    timerInfoText.setVisibility(View.GONE);
                    appInfoText.setVisibility(View.GONE);
                    favoriteTagText.setVisibility(View.GONE);
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
        allInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickedAllInfo) {
                    allInfoTable.setVisibility(View.VISIBLE);
                    isClickedAllInfo = true;

                } else {
                    allInfoTable.setVisibility(View.GONE);
                    isClickedAllInfo = false;
                }
            }
        });
    }

    public boolean isTagSelected(String tag) {
        return tagsToShow.get(tag);
    }

    private void showTagListView() {

        String[] tags = db.getTags();
        Map<String, Long> totalTimePerTag = getTotalTimePerTag(tags);
        List<Map.Entry<String, Long>> totalTimePerTagEntrySet = new ArrayList<Map.Entry<String, Long>>(
                totalTimePerTag.entrySet());

        sortEntrySet(totalTimePerTagEntrySet);

        TagListAdapter adapter = new TagListAdapter(view.getContext(), totalTimePerTagEntrySet, this);

        // Assign adapter to ListView
        tagListView.setAdapter(adapter);


        for (Map.Entry<String, Long> entry : totalTimePerTagEntrySet) {
            if (tagsToShow.get(entry.getKey())) {
                String tag = entry.getKey();

            }
        }

        setListViewHeightBasedOnChildren(tagListView);
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean keep = !tagsToShow.get(((Map.Entry<String, Long>) tagListView.getItemAtPosition(position)).getKey());
                tagsToShow.put(((Map.Entry<String, Long>) tagListView.getItemAtPosition(position)).getKey(), keep);
                tagListView.invalidateViews();
                showPieChart(true);
            }
        });
    }

    private void showPieChart(Boolean update) {

        String[] tags = db.getTags();
        Map<String, Long> totalTimePerTag = getTotalTimePerTag(tags);
        List<Map.Entry<String, Long>> totalTimePerTagEntrySet = new ArrayList<Map.Entry<String, Long>>(
                totalTimePerTag.entrySet());

        List<PieEntry> entries = new ArrayList<>();
        if (!update) {
            for (String tag : tags) {
                tagsToShow.put(tag, false);
                if (tags.length <= 5) {
                    tagsToShow.put(tag, true);

                }
            }
            if (tags.length > 5) {
                selectTopFive(totalTimePerTag);
            }
        }

        for (Map.Entry<String, Long> entry : totalTimePerTagEntrySet) {
            if (tagsToShow.get(entry.getKey())) {
                String tag = entry.getKey();
                Long value = entry.getValue();
                entries.add(new PieEntry(value, tag));
            }
        }


        PieDataSet set = new PieDataSet(entries, "Time per Tag");
        PieData data = new PieData(set);
        tagPieChart.setData(data);
        formatPieChart(data, set);

    }

    private void formatPieChart(PieData data, PieDataSet set) {

        IValueFormatter formatter = new IValueFormatter() {

            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return DateFormatterUtil.formatTime( (long) entry.getY());
            }
        };

        data.setValueFormatter(formatter);
        tagPieChart.setNoDataText("No Data");
        tagPieChart.animateY(500);
        tagPieChart.getLegend().setEnabled(false);
        set.setColors(ColorTemplate.JOYFUL_COLORS);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setSliceSpace(2f);
        set.setSelectionShift(0f);
        data.setValueTextSize(12f);
        data.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tagPieChart.invalidate(); // refresh

        Description des = tagPieChart.getDescription();
        des.setEnabled(false);

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
        sortEntrySet(list);

        Log.d(TAG, "getTopFive: " + list.size());
        topFive = list.subList(0, 5);
        for (Map.Entry<String, Long> tag : topFive) {
            tagsToShow.put(tag.getKey(), true);
        }
    }


    private String favoriteTag(){
        String[] tags = db.getTags();
        Map<String, Long> totalTimePerTag = getTotalTimePerTag(tags);
        List<Map.Entry<String, Long>> topFive;

        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(
                totalTimePerTag.entrySet());
        sortEntrySet(list);

        Log.d(TAG, "getTopFive: " + list.size());
        String favorite = list.subList(0, 1).get(0).getKey();
        return favorite;

    }

    private void sortEntrySet(List<Map.Entry<String, Long>> entrySet) {
        Collections.sort(entrySet, new Comparator<Map.Entry<String, Long>>() {

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
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void showAllData(DatabaseHelper db) {
        TableLayout.LayoutParams tableRowParams =
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        formatAllDataTable(tableRowParams);

        Cursor cursor = db.getData();
        cursor.moveToFirst();
        int max = cursor.getCount();
        List<Pair> allTimes = new ArrayList<>();

        for (int i = 1; i < max +1; i++) {

            allTimes.add(new Pair((cursor.getLong(cursor.getColumnIndex("start_time"))), cursor.getLong(cursor.getColumnIndex("duration"))));
            cursor.moveToNext();
        }
        cursor.close();

        Collections.sort(allTimes, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                if ((Long)o1.first < (Long)o2.first) {
                    return 1;
                } else if ((Long)o1.first > (Long)o2.first) {
                    return -1;
                }
                return 0;
            }
        });

        for (int i = 1; i < max + 1; i++) {
            tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(tableRowParams);
            textView_0 = new TextView(getContext());
            textView_1 = new TextView(getContext());
            textView_2 = new TextView(getContext());
            textView_0.setText(" " + i + " ");
            textView_0.setGravity(Gravity.CENTER);
            tableRow.addView(textView_0);

            LocalDateTime start = LocalDateTime.ofEpochSecond(((Long)allTimes.get(i-1).first), 0, ZoneOffset.UTC);
            textView_1.setText(start.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(new Locale("en", "IN"))));
            textView_1.setGravity(Gravity.START);
            tableRow.addView(textView_1);

            String duration = " " + DateFormatterUtil.formatTime((Long)allTimes.get(i-1).second) + "";
            textView_2.setText(duration);
            textView_2.setGravity(Gravity.END);
            tableRow.addView(textView_2);
            allInfoTable.addView(tableRow);

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

    private void formatAllDataTable(TableLayout.LayoutParams tableRowParams) {

        int leftMargin = 10;
        int topMargin = 10;
        int rightMargin = 5;
        int bottomMargin = 5;

        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        row02 = new TableRow(getContext());
        row02.setLayoutParams(tableRowParams);
        textView02 = new TextView(getContext());
        textView02.setText(" No. ");
        textView02.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        row02.addView(textView02);

        textView03 = new TextView(getContext());
        textView03.setText("      Start Time ");
        textView03.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        row02.addView(textView03);

        textView2 = new TextView(getContext());
        textView2.setText(" Duration ");
        textView2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        row02.addView(textView2);
        allInfoTable.addView(row02);
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

}
