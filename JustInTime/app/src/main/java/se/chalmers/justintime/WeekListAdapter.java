package se.chalmers.justintime;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.util.HashMap;
import java.util.Locale;

import se.chalmers.justintime.database.TimerInfoBundle;

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
        if (position%2 != 0) {    // Zebra striping
            Drawable background = view.getBackground();
            if (background instanceof ColorDrawable) {
                int bgColor = ((ColorDrawable) background).getColor();
                int red = Color.red(bgColor);
                red = red<205?red+50:255;
                int green = Color.green(bgColor);
                green = green<205?green+50:255;
                int blue = Color.blue(bgColor);
                blue = blue<205?blue+50:255;
                view.setBackgroundColor(Color.rgb(red,green,blue));
            }
        }

        StatisticsBundle week = weeks[position];

        TextView weekNbr = (TextView) view.findViewById(R.id.weekNbrTV);
        weekNbr.setText(week.getLabel());

        TextView year = (TextView) view.findViewById(R.id.weekYearTV);
        if (week.getTimers().length>0 && !week.getTimers()[0].getTimes().isEmpty()) {
            LocalDateTime date = week.getTimers()[0].getTimes().get(0).first;
            year.setText(String.format(Locale.ENGLISH, "%d", date.getYear()));
        } else {
            year.setText(R.string.error);
        }

        TextView totTime = (TextView) view.findViewById(R.id.weekTotalTimeTV);
        totTime.setText(DateFormatterUtil.formatTime(week.getTotalDuration()));

        HashMap<String, Long> tagDurations = new HashMap<>();
        long duration;
        for (TimerInfoBundle tib : week.getTimers()) {
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
            ((TextView) view.findViewById(R.id.weekTag1TV)).setText(tag1);
            ((TextView) view.findViewById(R.id.weekTag1TimeTV)).setText(DateFormatterUtil.formatTime(tagDurations.get(tag1)));
        } else {
            ((TextView) view.findViewById(R.id.weekTag1TV)).setText("");
            ((TextView) view.findViewById(R.id.weekTag1TimeTV)).setText("");
        }
        if (tag2 != null) {
            ((TextView) view.findViewById(R.id.weekTag2TV)).setText(tag2);
            ((TextView) view.findViewById(R.id.weekTag2TimeTV)).setText(DateFormatterUtil.formatTime(tagDurations.get(tag2)));
        } else {
            ((TextView) view.findViewById(R.id.weekTag2TV)).setText("");
            ((TextView) view.findViewById(R.id.weekTag2TimeTV)).setText("");
        }
        if (tagDurations.size()>2) {
            ((TextView) view.findViewById(R.id.weekTagOthersTV)).setText(R.string.others);
            ((TextView) view.findViewById(R.id.weekTagOthersTimeTV)).setText(DateFormatterUtil.formatTime(
                    week.getTotalDuration() - tagDurations.get(tag1) - tagDurations.get(tag2)));
        } else {
            ((TextView) view.findViewById(R.id.weekTagOthersTV)).setText("");
            ((TextView) view.findViewById(R.id.weekTagOthersTimeTV)).setText("");
        }

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
