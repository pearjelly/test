package me.pearjelly.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import me.pearjelly.wxrobot.net.pojo.DeviceInfo;
import me.pearjelly.wxrobot.net.pojo.Result;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity context = this;
        SharedPreferences prefs = getPrefs(context);
        if (TextUtils.isEmpty(prefs.getString("imei", null))) {
            Call<Result> deviceInfoCall = NetworkManager.getInstance().getInfoService().createInfo(DeviceInfo.getDeviceInfo(this));
            try {
                Response<Result> execute = deviceInfoCall.execute();
                Log.i(LOG_TAG,"response "+execute.body());
            } catch (IOException e) {
                Log.e(LOG_TAG,"execute error",e);
            }
//            deviceInfoCall.enqueue(new Callback<Result>() {
//                @Override
//                public void onResponse(Call<Result> call, Response<Result> response) {
//                    Log.i(LOG_TAG, "createInfo response " + response.body());
//                }
//
//                @Override
//                public void onFailure(Call<Result> call, Throwable t) {
//                    Log.e(LOG_TAG, "createInfo response error", t);
//                }
//            });
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
