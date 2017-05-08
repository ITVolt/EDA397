package se.chalmers.justintime.timer;

import android.os.Message;

/**
 * Created by Nieo on 04/05/17.
 */

interface Messager {
    void sendMessage(Message message);

    void showNotification(int id, CharSequence text);
}
