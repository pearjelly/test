package me.pearjelly.wxrobot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import me.pearjelly.wxrobot.service.WorkerService;

/**
 * Created by xiaobinghan on 16/4/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            context.startService(new Intent(context, WorkerService.class));
            Toast.makeText(context, "WorkerService service has started!", Toast.LENGTH_LONG).show();
        }
    }
}
