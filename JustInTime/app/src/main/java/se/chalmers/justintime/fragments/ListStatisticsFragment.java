package se.chalmers.justintime.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.List;

import se.chalmers.justintime.R;
import se.chalmers.justintime.alert.SharedPreference;
import se.chalmers.justintime.database.DatabaseHelper;

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
                if(!isClickedGeneral){
                    String totalDuration = db.getTotalDuration();
                    if(totalDuration==null)
                    {
                        totalDuration = "0 Hour";
                    }
                    timerInfoText.setText("Used for : " + totalDuration );
                    appInfoText.setText("Used : " + mPreferences.getAppUsageCount() + " times");
                    timerInfoText.setVisibility(View.VISIBLE);
                    appInfoText.setVisibility(View.VISIBLE);
                    isClickedGeneral = true;
                }
                else{
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

        List<PieEntry> entries = new ArrayList<>();

        for (String tag : tags) {
            entries.add(new PieEntry(18.5f, tag));
        }

//
//        entries.add(new PieEntry(18.5f, "Green"));
//        entries.add(new PieEntry(26.7f, "Yellow"));
//        entries.add(new PieEntry(24.0f, "Red"));
//        entries.add(new PieEntry(30.8f, "Blue"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
        PieData data = new PieData(set);
        tagPieChart.setData(data);
        tagPieChart.invalidate(); // refresh
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
