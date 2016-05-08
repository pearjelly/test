package me.pearjelly.smsreceiver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import me.pearjelly.smsreceiver.observer.SmsObserver;

public class SmsReceiver extends BroadcastReceiver {

    public static final String LOG_TAG = SmsReceiver.class.getName();

    public SmsReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                String phonenumber = msg.getOriginatingAddress();
                String messageBody = msg.getDisplayMessageBody();
                Log.i(LOG_TAG, "receive SMS from phonenumber:" + phonenumber + " body:" + messageBody);
                SmsObserver.uploadPhonenumber(context, phonenumber, messageBody, 0, null);
            }
        }
    }

}
