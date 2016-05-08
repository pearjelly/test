package me.pearjelly.common;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hxb on 2016/5/8.
 */
public class Util {


    public static final String LOG_TAG = Util.class.getName();

    public static void showMessage(Context context, TextView consoleTextView, String text) {
        if (consoleTextView != null) {
            consoleTextView.append(">>" + String.valueOf(text) + "\n");
        } else {
            if (context != null) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }
        Log.i(LOG_TAG, text);
    }
}
