package se.chalmers.justintime.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import se.chalmers.justintime.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekDetailedStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekDetailedStatisticsFragment extends Fragment {

    private View view;

    public WeekDetailedStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WeekDetailedStatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekDetailedStatisticsFragment newInstance(String fragmentName) {
        WeekDetailedStatisticsFragment fragment = new WeekDetailedStatisticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_week_statistics, container, false);
        showChart();

        return view;
    }
    private void showChart() {
        LineChart chart = (LineChart) view.findViewById(R.id.chart);

        // creating list of entry
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4f));
        entries.add(new Entry(1, 8f));
        entries.add(new Entry(2, 6f));
        entries.add(new Entry(3, 2f));
        entries.add(new Entry(4, 18f));
        entries.add(new Entry(5, 9f));
        entries.add(new Entry(6, 9f));

        LineDataSet dataSet = new LineDataSet(entries, "Time for each day of the week");

        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));

        ArrayList<ILineDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(dataSet);

        LineData data = new LineData(dataSetList);
        chart.setData(data);

        // the labels that should be drawn on the XAxis
        final String[] days = new String[] { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return days[(int) value];
            }
        };

        Description des = chart.getDescription();
        des.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(16f);
        xAxis.setTextColor(getResources().getColor(R.color.colorAccent));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(formatter);
        chart.animateXY(1000, 3000);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextSize(16f);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        Legend l = chart.getLegend();
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setTextSize(10f);
        l.setTextColor(getResources().getColor(R.color.divider));
    }

}
