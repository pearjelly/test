package me.pearjelly.wxrobot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import me.pearjelly.wxrobot.common.Constasts;
import me.pearjelly.wxrobot.service.WorkerService;

/**
 * Created by xiaobinghan on 16/4/16.
 */
public class MainActivity extends Activity {

    public static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        Context context = this;
        Intent service = new Intent(getApplicationContext(), WorkerService.class);
        stopService(service);
        startService(service);
        Toast.makeText(context, "wxrobot started!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }
}
