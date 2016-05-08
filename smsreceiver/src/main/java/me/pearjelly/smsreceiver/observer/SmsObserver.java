package me.pearjelly.smsreceiver.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.TextView;

import me.pearjelly.common.Util;
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
    private TextView consoleTextView;

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        getSmsFromPhone();
    }

    public void getSmsFromPhone() {
        ContentResolver cr = mContext.getContentResolver();
        String[] projection = new String[]{"_id", "address", "body"};//"_id", "address", "person",, "date", "type
        String where = " body like 'imsi:%' ";
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String body = cur.getString(cur.getColumnIndex("body"));
            long id = cur.getLong(cur.getColumnIndex("_id"));
            String msg = "接收到来自" + String.valueOf(number) + "的短信(id-" + String.valueOf(id) + ")，内容:\n" + String.valueOf(body);
            Util.showMessage(mContext, consoleTextView, msg);
            uploadPhonenumber(mContext, number, body, id, consoleTextView);
        }
    }

    public static void deleteSms(Context context, long id, TextView consoleTextView) {
        if (id > 0) {
            ContentResolver cr = context.getContentResolver();
            cr.delete(Uri.parse("content://sms"), "_id=" + String.valueOf(id), null);
            Util.showMessage(context, consoleTextView, "删除短信(id-" + String.valueOf(id) + ")");
        }
    }

    public static void uploadPhonenumber(final Context context, String phonenumber, String messageBody, final long id, final TextView consoleTextView) {
        if (!TextUtils.isEmpty(messageBody) && messageBody.startsWith("imsi:") && !TextUtils.isEmpty(phonenumber)) {
            final String imsi = messageBody.substring(5);
            if (phonenumber.startsWith("+86")) {
                phonenumber = phonenumber.substring(3);
            }
            if (!TextUtils.isEmpty(imsi)) {
                Util.showMessage(context, consoleTextView, "提取imsi:" + imsi + "和phonenumber:" + phonenumber);
                final NetworkManager networkManager = NetworkManager.getInstance();
                Call<GenPasswdResult> genPasswdResultCall = networkManager.getAccountService().genPasswd(phonenumber);
                final String finalPhonenumber = phonenumber;
                genPasswdResultCall.enqueue(new Callback<GenPasswdResult>() {
                    @Override
                    public void onResponse(Call<GenPasswdResult> call, Response<GenPasswdResult> response) {
                        GenPasswdResult body = response.body();
                        if (body != null && body.status.equals("100000") && !TextUtils.isEmpty(body.data.passwd)) {
                            Util.showMessage(context, consoleTextView, "为" + String.valueOf(finalPhonenumber) + "生成密码:" + String.valueOf(body.data.passwd));
                            Call<UploadResult> uploadPhonenumberCall = networkManager.getAccountService().uploadPhonenumber(finalPhonenumber, imsi, body.data.passwd);
                            uploadPhonenumberCall.enqueue(new Callback<UploadResult>() {
                                @Override
                                public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                                    if (id != 0) {
                                        deleteSms(context, id, consoleTextView);
                                    }
                                    Util.showMessage(context, consoleTextView, "上传成功:" + String.valueOf(response.body()));
                                }

                                @Override
                                public void onFailure(Call<UploadResult> call, Throwable t) {
                                    Util.showMessage(context, consoleTextView, "上传失败:" + String.valueOf(t));
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GenPasswdResult> call, Throwable t) {
                        Util.showMessage(context, consoleTextView, "获取密码:失败\n" + String.valueOf(finalPhonenumber) + "\n" + String.valueOf(t));
                    }
                });
            }
        }
    }

    public void setConsoleTextView(TextView consoleTextView) {
        this.consoleTextView = consoleTextView;
    }
}
