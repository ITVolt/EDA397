package se.chalmers.justintime;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import java.util.Locale;

/**
 * Handles the list of months in the statistics.
 * Created by Patrik on 2017-04-27.
 */

public class MonthListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private StatisticsBundle[] months;


    public MonthListAdapter(Context context, StatisticsBundle[] months) {
        this.context = context;
        this.months = months;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return months.length;
    }

    @Override
    public Object getItem(int position) {
        return months[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_statistics_month, null);
        }
        StatisticsBundle month = months[position];

        TextView monthTV = (TextView) view.findViewById(R.id.monthTV);
        monthTV.setText(month.getLabel());

        TextView year = (TextView) view.findViewById(R.id.monthYearTV);
        LocalDateTime date = month.getTimers()[0].getTimes().get(0).first;
        year.setText(String.format(Locale.ENGLISH, "%d", date.getYear()));

        TextView totTime = (TextView) view.findViewById(R.id.monthTotalTimeTV);
        totTime.setText(formatTime(month.getTotalDuration()));

        ((TextView) view.findViewById(R.id.monthTag1TV)).setText("");
        ((TextView) view.findViewById(R.id.monthTag1TimeTV)).setText("");
        ((TextView) view.findViewById(R.id.monthTag2TV)).setText("");
        ((TextView) view.findViewById(R.id.monthTag2TimeTV)).setText("");
        ((TextView) view.findViewById(R.id.monthTagOthersTV)).setText("");
        ((TextView) view.findViewById(R.id.monthTagOthersTimeTV)).setText("");

        return view;
    }

    private String formatTime(long time) {
        StringBuilder text = new StringBuilder();
        text.setLength(0);

        // 1 d 12 h
        if (time >= DateUtils.DAY_IN_MILLIS) {
            int days = (int) (time/DateUtils.DAY_IN_MILLIS);
            text.append(days).append(" d ");
            time -= days*DateUtils.DAY_IN_MILLIS;
            text.append(time/DateUtils.HOUR_IN_MILLIS).append(" h");
        // 12 h 54 m
        } else if (time >= DateUtils.HOUR_IN_MILLIS) {
            int hours = (int) (time/DateUtils.HOUR_IN_MILLIS);
            text.append(hours).append(" h ");
            time -= hours*DateUtils.HOUR_IN_MILLIS;
            text.append(time/DateUtils.MINUTE_IN_MILLIS).append(" m");
        // 54 m 32 s
        } else if (time >= DateUtils.MINUTE_IN_MILLIS) {
            int minutes = (int) (time/DateUtils.MINUTE_IN_MILLIS);
            text.append(minutes).append(" m ");
            time -= minutes*DateUtils.MINUTE_IN_MILLIS;
            text.append(time/DateUtils.SECOND_IN_MILLIS).append(" s");
        // 32 s
        } else if (time >= DateUtils.SECOND_IN_MILLIS) {
            text.append(time/DateUtils.SECOND_IN_MILLIS).append(" s");
        // -
        } else {
            text.append("-");
        }

        return text.toString();
    }
}
