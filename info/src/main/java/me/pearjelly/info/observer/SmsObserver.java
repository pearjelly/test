package me.pearjelly.info.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import me.pearjelly.wxrobot.net.pojo.GenPasswdResult;
import me.pearjelly.wxrobot.net.pojo.UploadResult;
import me.pearjelly.wxrobot.net.service.NetworkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hxb on 2016/5/8.
 */
public class SmsObserver extends ContentObserver {
    public static final String LOG_TAG = SmsObserver.class.getName();
    public static Uri SMS_INBOX = Uri.parse("content://sms/");
    private Context mContext;
    private Handler mHandler;

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //每当有新短信到来时，使用我们获取短消息的方法
        getSmsFromPhone();
    }

    public void getSmsFromPhone() {
        ContentResolver cr = mContext.getContentResolver();
        String[] projection = new String[]{"thread_id", "address", "body"};//"_id", "address", "person",, "date", "type
        String where = " body like 'imsi:%' AND date >  " + (System.currentTimeMillis() - 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String body = cur.getString(cur.getColumnIndex("body"));
            long threadId = cur.getLong(cur.getColumnIndex("thread_id"));
            Log.i(LOG_TAG, "receive SMS from phonenumber:" + number + " body:" + body);
            uploadPhonenumber(mContext, number, body, threadId);
        }
    }

    public static void deleteSms(Context context, long threadId) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
        Log.d("deleteSMS", "threadId:: " + threadId);
    }

    public static void uploadPhonenumber(final Context context, String phonenumber, String messageBody, final long threadId) {
        if (!TextUtils.isEmpty(messageBody) && messageBody.startsWith("imsi:") && !TextUtils.isEmpty(phonenumber)) {
            final String imsi = messageBody.substring(5);
            if (phonenumber.startsWith("+86")) {
                phonenumber = phonenumber.substring(3);
            }
            if (!TextUtils.isEmpty(imsi)) {
                Log.i(LOG_TAG, "receive imsi:" + imsi + " of phonenumber: " + phonenumber);
                final NetworkManager networkManager = NetworkManager.getInstance();
                Call<GenPasswdResult> genPasswdResultCall = networkManager.getAccountService().genPasswd(phonenumber);
                final String finalPhonenumber = phonenumber;
                genPasswdResultCall.enqueue(new Callback<GenPasswdResult>() {
                    @Override
                    public void onResponse(Call<GenPasswdResult> call, Response<GenPasswdResult> response) {
                        GenPasswdResult body = response.body();
                        if (body != null && body.status.equals("100000") && !TextUtils.isEmpty(body.data.passwd)) {
                            Toast.makeText(context, "获取密码：\n" + String.valueOf(finalPhonenumber) + "\n" + String.valueOf(body.data.passwd), Toast.LENGTH_LONG).show();
                            Call<UploadResult> uploadPhonenumberCall = networkManager.getAccountService().uploadPhonenumber(finalPhonenumber, imsi, body.data.passwd);
                            uploadPhonenumberCall.enqueue(new Callback<UploadResult>() {
                                @Override
                                public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                                    Log.i(LOG_TAG, "uploadPhonenumber response " + response.body());
                                    if (threadId != 0) {
                                        deleteSms(context, threadId);
                                    }
                                    Toast.makeText(context, "上传成功：" + String.valueOf(response.body()), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(Call<UploadResult> call, Throwable t) {
                                    Log.e(LOG_TAG, "uploadPhonenumber response error", t);
                                    Toast.makeText(context, "上传失败：" + String.valueOf(t), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GenPasswdResult> call, Throwable t) {
                        Toast.makeText(context, "获取密码：失败\n" + String.valueOf(finalPhonenumber) + "\n" + String.valueOf(t), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
