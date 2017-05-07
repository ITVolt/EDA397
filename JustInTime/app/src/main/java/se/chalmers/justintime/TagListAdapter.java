package se.chalmers.justintime;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.threeten.bp.LocalDateTime;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import se.chalmers.justintime.R;
import se.chalmers.justintime.StatisticsBundle;

/**
 * Created by Patrik on 2017-04-18.
 */

public class TagListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Map.Entry<String, Long>> tags;


    public TagListAdapter(Context context, List<Map.Entry<String, Long>> entries) {
        this.context = context;
        this.tags = entries;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Map.Entry<String, Long> getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_statistics_tags, null);
        }
        Map.Entry<String, Long> entry = tags.get(position);

        TextView tag = (TextView) view.findViewById(R.id.tag);
        tag.setText(entry.getKey());

        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(formatTime(entry.getValue()));

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
            int hours = (int) (time/DateUtils.HOUR_IN_MILLIS);
            if (hours < 10) text.append("0");
            text.append(hours).append(" h");
        // 12 h 54 m
        } else if (time >= DateUtils.HOUR_IN_MILLIS) {
            int hours = (int) (time/DateUtils.HOUR_IN_MILLIS);
            text.append(hours).append(" h ");
            time -= hours*DateUtils.HOUR_IN_MILLIS;
            int minutes = (int) (time/DateUtils.MINUTE_IN_MILLIS);
            if (minutes < 10) text.append("0");
            text.append(minutes).append(" m");
        // 54 m 32 s
        } else if (time >= DateUtils.MINUTE_IN_MILLIS) {
            int minutes = (int) (time/DateUtils.MINUTE_IN_MILLIS);
            text.append(minutes).append(" m ");
            time -= minutes*DateUtils.MINUTE_IN_MILLIS;
            int seconds = (int) (time/DateUtils.SECOND_IN_MILLIS);
            if (seconds < 10) text.append("0");
            text.append(seconds).append(" s");
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
