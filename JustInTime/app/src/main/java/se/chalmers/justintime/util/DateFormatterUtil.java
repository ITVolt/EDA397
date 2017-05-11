package se.chalmers.justintime.util;

import android.text.format.DateUtils;

/**
 * Created by Felix on 2017-05-09.
 */

public class DateFormatterUtil {
    public static String formatMillisecondsToShortTimeString(long time) {
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
