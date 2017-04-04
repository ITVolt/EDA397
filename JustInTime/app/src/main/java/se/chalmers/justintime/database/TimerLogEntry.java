package se.chalmers.justintime.database;

import org.threeten.bp.LocalDateTime;

/**
 * Created by David on 2017-04-04.
 */

public class TimerLogEntry {
    int id;
    int groupId;
    LocalDateTime startTime;
    long duration;
}
