package se.chalmers.justintime;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import java.util.HashMap;
import java.util.Locale;

import se.chalmers.justintime.database.TimerInfoBundle;

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
        if (position%2 != 0) {    // Zebra striping
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                int bgColor = ((ColorDrawable) background).getColor();
                int red = Color.red(bgColor);
                red = red<245?red+10:255;
                int green = Color.green(bgColor);
                green = green<245?green+10:255;
                int blue = Color.blue(bgColor);
                blue = blue<245?blue+10:255;
                view.setBackgroundColor(Color.rgb(red,green,blue));
            }
        }

        StatisticsBundle month = months[position];

        TextView monthTV = (TextView) view.findViewById(R.id.monthTV);
        monthTV.setText(month.getLabel());

        TextView year = (TextView) view.findViewById(R.id.monthYearTV);
        LocalDateTime date = month.getTimers()[0].getTimes().get(0).first;
        year.setText(String.format(Locale.ENGLISH, "%d", date.getYear()));

        TextView totTime = (TextView) view.findViewById(R.id.monthTotalTimeTV);
        totTime.setText(DateFormatterUtil.formatTime(month.getTotalDuration()));

        HashMap<String, Long> tagDurations = new HashMap<>();
        long duration;
        for (TimerInfoBundle tib : month.getTimers()) {
            String[] tags = tib.getTags();
            duration = tib.getTotalDuration();
            for (String tag : tags) {
                if (!tagDurations.containsKey(tag)) {
                    tagDurations.put(tag, 0L);
                }
                long tmp = tagDurations.get(tag);
                tagDurations.put(tag, tmp+duration);
            }
        }
        String tag1 = null, tag2 = null;
        long highest = 0;
        for (String tag : tagDurations.keySet()) {
            duration = tagDurations.get(tag);
            if (duration > highest) {
                highest = duration;
                if (tag1 != null) {
                    tag2 = tag1;
                }
                tag1 = tag;
            }
        }

        if (tag1 != null) {
            ((TextView) view.findViewById(R.id.monthTag1TV)).setText(shortenString(tag1));
            ((TextView) view.findViewById(R.id.monthTag1TimeTV)).setText(DateFormatterUtil.formatTime(tagDurations.get(tag1)));
        } else {
            ((TextView) view.findViewById(R.id.monthTag1TV)).setText("");
            ((TextView) view.findViewById(R.id.monthTag1TimeTV)).setText("");
        }
        if (tag2 != null) {
            ((TextView) view.findViewById(R.id.monthTag2TV)).setText(shortenString(tag2));
            ((TextView) view.findViewById(R.id.monthTag2TimeTV)).setText(DateFormatterUtil.formatTime(tagDurations.get(tag2)));
        } else {
            ((TextView) view.findViewById(R.id.monthTag2TV)).setText("");
            ((TextView) view.findViewById(R.id.monthTag2TimeTV)).setText("");
        }
        if (tagDurations.size()>2) {
            ((TextView) view.findViewById(R.id.monthTagOthersTV)).setText(R.string.others);
            ((TextView) view.findViewById(R.id.monthTagOthersTimeTV)).setText(DateFormatterUtil.formatTime(
                    month.getTotalDuration() - tagDurations.get(tag1) - tagDurations.get(tag2)));
        } else {
            ((TextView) view.findViewById(R.id.monthTagOthersTV)).setText("");
            ((TextView) view.findViewById(R.id.monthTagOthersTimeTV)).setText("");
        }

        return view;
    }

    private String shortenString(String str) {
        if (str.length()>10) {
            str = str.substring(0, 10);
            str = str + "...";
        }
        return str;
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
