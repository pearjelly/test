package me.pearjelly.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import me.pearjelly.common.Util;
import me.pearjelly.wxrobot.net.pojo.DeviceInfo;
import me.pearjelly.wxrobot.net.pojo.GenPasswdResult;
import me.pearjelly.wxrobot.net.pojo.InfoResult;
import me.pearjelly.wxrobot.net.pojo.UploadResult;
import me.pearjelly.wxrobot.net.service.NetworkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView consoleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DeviceInfo deviceInfo = DeviceInfo.getDeviceInfo(this);
        setEditText(R.id.imei, deviceInfo.getImei());
        setEditText(R.id.imsi, deviceInfo.getImsi());
        setEditText(R.id.number, deviceInfo.getPhonenumber());
        setEditText(R.id.simcountryiso, deviceInfo.getSimcountryiso());
        setEditText(R.id.netcountryiso, deviceInfo.getNetcountryiso());
        setEditText(R.id.simserial, deviceInfo.getSimserial());
        setEditText(R.id.wifimac, deviceInfo.getWifimac());
        setEditText(R.id.bluemac, deviceInfo.getBluemac());
        setEditText(R.id.androidid, deviceInfo.getAndroidid());
        setEditText(R.id.serial, deviceInfo.getSerial());
        setEditText(R.id.brand, deviceInfo.getBrand());
        setEditText(R.id.manufacturer, deviceInfo.getManufacturer());
        setEditText(R.id.model, deviceInfo.getModel());
        setEditText(R.id.simoperator, deviceInfo.getSimoperator());
        setEditText(R.id.simoperatorname, deviceInfo.getSimoperatorname());
        setEditText(R.id.voicecapable, String.valueOf(deviceInfo.getVoicecapable()));
        setEditText(R.id.phonetype, String.valueOf(deviceInfo.getPhonetype()));
        setEditText(R.id.simstate, String.valueOf(deviceInfo.getSimstate()));

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        consoleTextView = (TextView) findViewById(R.id.consoleTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final MainActivity context = this;
        final DeviceInfo deviceInfo = DeviceInfo.getDeviceInfo(this);
        Call<InfoResult> infoResultCall = NetworkManager.getInstance().getInfoService().getExtInfo(deviceInfo.imei);
        infoResultCall.enqueue(new Callback<InfoResult>() {
            @Override
            public void onResponse(Call<InfoResult> call, Response<InfoResult> response) {
                DeviceInfo remoteDeviceInfo = response.body().data;
                if (response.body() != null && remoteDeviceInfo.diid == 0) {
                    uploadDeviceInfoWithPhoneNumber(context);
                } else {
                    Util.showMessage(context, consoleTextView, "本次上传已忽略，服务器已存在imei=" + String.valueOf(deviceInfo.imei) + "的数据，与当前数据" + (deviceInfo.equals(remoteDeviceInfo) ? "相同" : "不同"));
                    Util.showMessage(context, consoleTextView, "服务器数据:\n" + String.valueOf(remoteDeviceInfo));
                }
            }

            @Override
            public void onFailure(Call<InfoResult> call, Throwable t) {
            }
        });
    }

    private void uploadDeviceInfoWithPhoneNumber(final Context context) {
        SharedPreferences prefs = getPrefs(context);
        if (TextUtils.isEmpty(prefs.getString("imei", null))) {
            final DeviceInfo deviceInfo = DeviceInfo.getDeviceInfo(this);
            final NetworkManager networkManager = NetworkManager.getInstance();
            if (TextUtils.isEmpty(deviceInfo.phonenumber)) {
                uploadDeviceInfo(context, deviceInfo);
            } else {
                Call<GenPasswdResult> genPasswdResultCall = networkManager.getAccountService().genPasswd(deviceInfo.phonenumber);
                genPasswdResultCall.enqueue(new Callback<GenPasswdResult>() {
                    @Override
                    public void onResponse(Call<GenPasswdResult> call, Response<GenPasswdResult> response) {
                        GenPasswdResult body = response.body();
                        if (body != null && body.status.equals("100000") && !TextUtils.isEmpty(body.data.passwd)) {
                            deviceInfo.setWxpasswd(body.data.passwd);
                            Util.showMessage(context, consoleTextView, "获取密码：\n" + String.valueOf(deviceInfo.phonenumber) + "\n" + String.valueOf(deviceInfo.wxpasswd));
                            uploadDeviceInfo(context, deviceInfo);
                        }
                    }

                    @Override
                    public void onFailure(Call<GenPasswdResult> call, Throwable t) {
                        Util.showMessage(context, consoleTextView, "获取密码：失败\n" + String.valueOf(deviceInfo.phonenumber) + "\n" + String.valueOf(t));
                    }
                });
            }
        } else {
            Util.showMessage(context, consoleTextView, "上传失败：非原始数据");
        }
    }

    private void uploadDeviceInfo(final Context context, final DeviceInfo deviceInfo) {
        NetworkManager networkManager = NetworkManager.getInstance();
        Call<UploadResult> deviceInfoCall = networkManager.getInfoService()
                .createInfo(deviceInfo.serial, deviceInfo.imei, deviceInfo.wifimac
                        , deviceInfo.bluemac, deviceInfo.androidid, deviceInfo.brand
                        , deviceInfo.manufacturer, deviceInfo.model
                        , deviceInfo.netcountryiso, deviceInfo.simcountryiso
                        , deviceInfo.phonenumber, deviceInfo.imsi, deviceInfo.simserial
                        , deviceInfo.wxpasswd);
        deviceInfoCall.enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                Util.showMessage(context, consoleTextView, "上传成功：" + String.valueOf(response.body()));
                if (TextUtils.isEmpty(deviceInfo.phonenumber) && !TextUtils.isEmpty(deviceInfo.imsi)) {
                    sendImsi(deviceInfo.imsi);
                }
            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {
                Util.showMessage(context, consoleTextView, "上传失败：" + String.valueOf(t));
            }
        });
    }

    public void setEditText(int resId, String value) {
        TextView textView = (TextView) findViewById(resId);
        textView.setText(value);
    }

    public String getEditTextString(int resId) {
        TextView viewById = (TextView) findViewById(resId);
        return viewById.getText().toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveDeviceInfo();
                break;
            case R.id.upload:
                uploadDeviceInfoWithPhoneNumber(this);
                break;
            default:
        }
    }

    private void saveDeviceInfo() {
        Context context = getApplicationContext();
        SharedPreferences preferences = getPrefs(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("imei", getEditTextString(R.id.imei));
        edit.putString("imsi", getEditTextString(R.id.imsi));
        String mobile = getEditTextString(R.id.number);
        edit.putString("number", mobile == null || mobile.length() == 0 ? "" : mobile.startsWith("+86") ? mobile : "+86" + mobile);
        edit.putString("netcountryiso", getEditTextString(R.id.netcountryiso));
        edit.putString("simcountryiso", getEditTextString(R.id.simcountryiso));
        edit.putString("simserial", getEditTextString(R.id.simserial));
        edit.putString("wifimac", getEditTextString(R.id.wifimac));
        edit.putString("bluemac", getEditTextString(R.id.bluemac));
        edit.putString("androidid", getEditTextString(R.id.androidid));
        edit.putString("serial", getEditTextString(R.id.serial));
        edit.putString("brand", getEditTextString(R.id.brand));
        edit.putString("manufacturer", getEditTextString(R.id.manufacturer));
        edit.putString("model", getEditTextString(R.id.model));

        edit.putString("simoperator", getEditTextString(R.id.simoperator));
        edit.putString("simoperatorname", getEditTextString(R.id.simoperatorname));
        String textString = getEditTextString(R.id.voicecapable);
        if (!TextUtils.isEmpty(textString) && !"null".equals(textString)) {
            edit.putString("voicecapable", textString);
        }
        String textString1 = getEditTextString(R.id.phonetype);
        if (!TextUtils.isEmpty(textString1) && !"null".equals(textString1)) {
            edit.putString("phonetype", textString1);
        }
        String textString2 = getEditTextString(R.id.simstate);
        if (!TextUtils.isEmpty(textString2) && !"null".equals(textString2)) {
            edit.putString("simstate", textString2);
        }
        edit.apply();
        Util.showMessage(context, consoleTextView, "保存数据");
    }

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("prefs", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
    }

    private void sendImsi(String imsi) {
        if (!TextUtils.isEmpty(imsi)) {
            String phone = "15550420424";
            SmsManager sms = SmsManager.getDefault();
            String text = "imsi:" + imsi;
            sms.sendTextMessage(phone, null, text, null, null);
            Util.showMessage(this, consoleTextView, "未获取到手机号码，发送绑定短信\"" + text + "\"到" + phone);
        }
    }
}
