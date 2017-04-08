package se.chalmers.justintime.alert;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Created by David on 2017-04-01.
 */

public class SoundAlarm extends AlarmDecorator {
    private final MediaPlayer mediaPlayer;

    public SoundAlarm(Alarm alarm, Context context) {
        super(alarm, context);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mediaPlayer = MediaPlayer.create(context, uri);
    }

    @Override
    public void alert() {
        super.alert();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void dismissAlarm() {
        super.dismissAlarm();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
