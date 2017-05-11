package se.chalmers.justintime.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.justintime.R;
import se.chalmers.justintime.util.StatisticsBundle;
import se.chalmers.justintime.database.TimerInfoBundle;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthDetailedStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthDetailedStatisticsFragment extends Fragment{

    private View view;
    StatisticsBundle data;
    public Button button;

    public MonthDetailedStatisticsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WeekDetailedStatisticsFragment.
     */
    public static MonthDetailedStatisticsFragment newInstance() {
        return new MonthDetailedStatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week_statistics_detailed_line_chart, container, false);
        showChart();

        button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(
                        R.id.relativeLayoutForFragment, StatisticsFragment.newInstance()).commit();
            }
        });
        return view;
    }

    public void setData(StatisticsBundle data) {
        this.data = data;

    }

    private void showChart() {
        LineChart chart = (LineChart) view.findViewById(R.id.chart);

        ArrayList<Entry> entries = createEntries(data);

        LineDataSet dataSet = new LineDataSet(entries, "Seconds timed for each day of the week");
        formatDataSet(dataSet);

        ArrayList<ILineDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(dataSet);

        LineData lineData = new LineData(dataSetList);
        chart.setData(lineData);

        formatChart(chart);

    }

    private void formatChart(LineChart chart) {

        Description des = chart.getDescription();
        des.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(16f);
        xAxis.setTextColor(getResources().getColor(R.color.colorAccent));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(1f);
        //xAxis.setValueFormatter(formatter);
        chart.animateY(300);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextSize(16f);
        yAxisLeft.setTextColor(getResources().getColor(R.color.colorAccent));
        yAxisLeft.setAxisMinimum(0f);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        Legend l = chart.getLegend();
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setTextSize(10f);
        l.setTextColor(getResources().getColor(R.color.divider));
    }

    private void formatDataSet(LineDataSet dataSet) {
        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));
        dataSet.setDrawValues(false);
    }

    private ArrayList<Entry> createEntries(StatisticsBundle data) {
        Map<Integer, Float> days = new HashMap<>();

        for (int i = 1; i <= 31; i++){
            days.put(i, 0f);
        }

        TimerInfoBundle[] timers = data.getTimers();
        int day;
        for (TimerInfoBundle temp : timers) {
            day = temp.getTimes().get(0).first.getDayOfMonth();
            days.put(day, days.get(day) + temp.getTotalDuration());
        }
        // creating list of entry
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 1; i <= 31; i++){
            entries.add(new Entry(i, days.get(i)/1000));
        }
        return entries;
    }


}
