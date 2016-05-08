package me.pearjelly.smsreceiver;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import me.pearjelly.smsreceiver.observer.SmsObserver;

public class MainActivity extends AppCompatActivity {

    private SmsObserver smsObserver;
    public Handler smsHandler = new Handler() {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView consoleTextView = (TextView) findViewById(R.id.console);
        smsObserver = new SmsObserver(this, smsHandler);
        smsObserver.setConsoleTextView(consoleTextView);
        getContentResolver().registerContentObserver(SmsObserver.SMS_INBOX, true, smsObserver);
    }
}
