package me.pearjelly.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.pearjelly.wxrobot.net.pojo.DeviceInfo;
import me.pearjelly.wxrobot.net.pojo.GenPasswdResult;
import me.pearjelly.wxrobot.net.pojo.UploadResult;
import me.pearjelly.wxrobot.net.service.NetworkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = MainActivity.class.getName();

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
        setEditText(R.id.brand, deviceInfo.getSerial());
        setEditText(R.id.manufacturer, deviceInfo.getManufacturer());
        setEditText(R.id.model, deviceInfo.getModel());
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final MainActivity context = this;
        uploadDeviceInfo(context);
    }

    private void uploadDeviceInfo(final Context context) {
        SharedPreferences prefs = getPrefs(context);
        if (TextUtils.isEmpty(prefs.getString("imei", null))) {
            final DeviceInfo deviceInfo = DeviceInfo.getDeviceInfo(this);
            final NetworkManager networkManager = NetworkManager.getInstance();
            Call<GenPasswdResult> genPasswdResultCall = networkManager.getAccountService().genPasswd(deviceInfo.phonenumber);
            genPasswdResultCall.enqueue(new Callback<GenPasswdResult>() {
                @Override
                public void onResponse(Call<GenPasswdResult> call, Response<GenPasswdResult> response) {
                    GenPasswdResult body = response.body();
                    if (body != null && body.status.equals("100000") && !TextUtils.isEmpty(body.data.passwd)) {
                        deviceInfo.setWxpasswd(body.data.passwd);
                        Toast.makeText(context, "获取密码：\n" + String.valueOf(deviceInfo.phonenumber) + "\n" + String.valueOf(deviceInfo.wxpasswd), Toast.LENGTH_LONG).show();
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
                                Log.i(LOG_TAG, "createInfo response " + response.body());
                                Toast.makeText(context, "上传成功：" + String.valueOf(response.body()), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(Call<UploadResult> call, Throwable t) {
                                Log.e(LOG_TAG, "createInfo response error", t);
                                Toast.makeText(context, "上传失败：" + String.valueOf(t), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<GenPasswdResult> call, Throwable t) {
                    Toast.makeText(context, "获取密码：失败\n" + String.valueOf(deviceInfo.phonenumber) + "\n" + String.valueOf(t), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(context, "上传失败：非原始数据", Toast.LENGTH_LONG).show();
        }
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
                uploadDeviceInfo(this);
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
        edit.putString("number", getEditTextString(R.id.number));
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
        edit.apply();
    }

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("prefs", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
    }
}
