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
 * Created by Patrik on 2017-04-18.
 */

public class WeekListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private StatisticsBundle[] weeks;


    public WeekListAdapter(Context context, StatisticsBundle[] sb) {
        this.context = context;
        this.weeks = sb;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return weeks.length;
    }

    @Override
    public Object getItem(int position) {
        return weeks[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_statistics_week, null);
        }
        StatisticsBundle week = weeks[position];

        TextView weekNbr = (TextView) view.findViewById(R.id.weekNbrTV);
        weekNbr.setText(week.getLabel());

        TextView year = (TextView) view.findViewById(R.id.weekYearTV);
        LocalDateTime date = week.getTimers()[0].getTimes().get(0).first;
        year.setText(String.format(Locale.ENGLISH, "%d", date.getYear()));

        TextView totTime = (TextView) view.findViewById(R.id.weekTotalTimeTV);
        totTime.setText(DateFormatterUtil.formatTime(week.getTotalDuration()));

        ((TextView) view.findViewById(R.id.weekTag1TV)).setText("");
        ((TextView) view.findViewById(R.id.weekTag1TimeTV)).setText("");
        ((TextView) view.findViewById(R.id.weekTag2TV)).setText("");
        ((TextView) view.findViewById(R.id.weekTag2TimeTV)).setText("");
        ((TextView) view.findViewById(R.id.weekTagOthersTV)).setText("");
        ((TextView) view.findViewById(R.id.weekTagOthersTimeTV)).setText("");

        return view;
    }

//    private String formatTime(long time) {
//        StringBuilder text = new StringBuilder();
//        text.setLength(0);
//
//        // 1 d 12 h
//        if (time >= DateUtils.DAY_IN_MILLIS) {
//            int days = (int) (time/DateUtils.DAY_IN_MILLIS);
//            text.append(days).append(" d ");
//            time -= days*DateUtils.DAY_IN_MILLIS;
//            text.append(time/DateUtils.HOUR_IN_MILLIS).append(" h");
//        // 12 h 54 m
//        } else if (time >= DateUtils.HOUR_IN_MILLIS) {
//            int hours = (int) (time/DateUtils.HOUR_IN_MILLIS);
//            text.append(hours).append(" h ");
//            time -= hours*DateUtils.HOUR_IN_MILLIS;
//            text.append(time/DateUtils.MINUTE_IN_MILLIS).append(" m");
//        // 54 m 32 s
//        } else if (time >= DateUtils.MINUTE_IN_MILLIS) {
//            int minutes = (int) (time/DateUtils.MINUTE_IN_MILLIS);
//            text.append(minutes).append(" m ");
//            time -= minutes*DateUtils.MINUTE_IN_MILLIS;
//            text.append(time/DateUtils.SECOND_IN_MILLIS).append(" s");
//        // 32 s
//        } else if (time >= DateUtils.SECOND_IN_MILLIS) {
//            text.append(time/DateUtils.SECOND_IN_MILLIS).append(" s");
//        // -
//        } else {
//            text.append("-");
//        }
//
//        return text.toString();
//    }
}
