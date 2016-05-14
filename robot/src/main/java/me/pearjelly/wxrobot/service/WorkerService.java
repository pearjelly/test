package me.pearjelly.wxrobot.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ShellUtils;
import me.pearjelly.wxrobot.MainActivity;
import me.pearjelly.wxrobot.R;
import me.pearjelly.wxrobot.common.Util;
import me.pearjelly.wxrobot.data.RobotContentResolver;
import me.pearjelly.wxrobot.net.pojo.DeviceInfo;
import me.pearjelly.wxrobot.net.pojo.InfoResult;
import me.pearjelly.wxrobot.net.service.NetworkManager;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by xiaobinghan on 16/4/16.
 */
public class WorkerService extends Service implements Runnable {

    public static final String LOG_TAG = WorkerService.class.getName();
    private Thread workThread;
    public static final String WX_APK = "mm-debug.apk";
    public static final String WX_PACKAGE = "com.tencent.mm";
    public static final String WX_TEST_APK = "mm-test-debug.apk";
    public static final String WX_TEST_PACKAGE = "com.tencent.mm.test";
    private String currentImei;

    public WorkerService() {
        super();
        currentImei = "0";
        Log.d(LOG_TAG, "init");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);
        Log.d(LOG_TAG, "onStartCommand");
        Context context = this;
        Util.wakeScreen(context);
        Util.unlock(context);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Title")
                .setContentText("Message")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(12346, noti);
        if (workThread == null) {
            workThread = new Thread(this);
        }
        if (!workThread.isAlive()) {
            workThread.start();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(LOG_TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(LOG_TAG, "onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean b = super.onUnbind(intent);
        Log.d(LOG_TAG, "onUnbind");
        return b;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(LOG_TAG, "onRebind");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(LOG_TAG, "onTaskRemoved");
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
        Log.d(LOG_TAG, "dump");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder o = null;
        Log.d(LOG_TAG, "onBind");
        return o;
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "wifi mac:" + String.valueOf(getLocalMacAddress()));
        final Context context = getApplicationContext();
        boolean initApks = initApks(context, true);
        if (initApks) {
            while (true) {
                nextTask(context);
            }
        }
    }

    private void nextTask(final Context context) {
        Call<InfoResult> infoResultCall = NetworkManager.getInstance().getInfoService().getNewInfo(currentImei);
        try {
            Response<InfoResult> response = infoResultCall.execute();
            DeviceInfo deviceInfo = response.body().data;
            currentImei = deviceInfo.imei;
            if (!TextUtils.isEmpty(currentImei) && !TextUtils.isEmpty(deviceInfo.phonenumber) && !TextUtils.isEmpty(deviceInfo.wxpasswd)) {
                try {
                    loginUseAccount(context, deviceInfo);
                    Log.d(LOG_TAG, "我还在运行..");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Thread.currentThread().sleep((long) (Math.random() * 1 * 60 * 1000));
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            } else {
                Log.e(LOG_TAG, "ignore data invalid:" + String.valueOf(deviceInfo));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void loginUseAccount(Context context, DeviceInfo deviceInfo) {
        String mobile = deviceInfo.getPhonenumber();
        RobotContentResolver contentResolver = new RobotContentResolver(context);
        contentResolver.insertAccount(deviceInfo);
        Context infoContext = null;
        try {
            infoContext = createPackageContext("me.pearjelly.info", Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences preferences = infoContext.getSharedPreferences("prefs", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
            Log.d(LOG_TAG, "get me.pearjelly.info preferences:" + String.valueOf(preferences));
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("imei", deviceInfo.getImei());
            edit.putString("wifimac", deviceInfo.getWifimac());
            edit.putString("bluemac", deviceInfo.getBluemac());
            edit.putString("androidid", deviceInfo.getAndroidid());
            edit.putString("serial", deviceInfo.getSerial());
            edit.putString("brand", deviceInfo.getBrand());
            edit.putString("netcountryiso", deviceInfo.getNetcountryiso());
            edit.putString("simcountryiso", deviceInfo.getSimcountryiso());
            edit.putString("manufacturer", deviceInfo.getManufacturer());
            edit.putString("model", deviceInfo.getModel());
//            edit.putString("number", mobile == null || mobile.length() == 0 ? "" : mobile.startsWith("+86") ? mobile : "+86" + mobile);
            edit.putString("number", "");//empty phone number
            edit.putString("imsi", deviceInfo.getImsi());
            edit.putString("simserial", deviceInfo.getSimserial());
            edit.apply();
            Log.d(LOG_TAG, "write account info:" + String.valueOf(deviceInfo));
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(LOG_TAG, "createPackageContext error " + String.valueOf(e.getMessage()));
        }

        resetwx();
        ShellUtils.CommandResult result = ShellUtils.execCommand("am instrument -w -r -e debug false -e class com.tencent.mm.test.LoginWithMobile com.tencent.mm.test/android.test.InstrumentationTestRunner", true);
        Log.d(LOG_TAG, "exec command mobile:" + mobile + "\nresult:" + result.successMsg + "\nerror:" + result.errorMsg);
    }

    public boolean initApks(Context context, boolean overwrite) {
        final String[] packages = {WX_PACKAGE, WX_TEST_PACKAGE};
        for (String pg : packages) {
            int result = PackageUtils.uninstallSilent(context, pg);
            Log.d(LOG_TAG, "uninstall package:" + pg + " " + (result == PackageUtils.DELETE_SUCCEEDED ? "success" : "failure"));

        }
        final String[] apks = {WX_APK, WX_TEST_APK};
        String[] list = context.getFilesDir().list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                for (String apkName : apks) {
                    if (apkName.equals(s)) {
                        return true;
                    }
                }
                return false;
            }
        });
        boolean success = false;
        if (overwrite || list == null || list.length < apks.length) {
            for (String apkName : apks) {
                String filePath = copyApk(context, apkName, overwrite);
                if (filePath != null && filePath.length() > 0) {

                    int result = PackageUtils.installSilent(context, filePath);
                    Log.d(LOG_TAG, "install " + (result == PackageUtils.INSTALL_SUCCEEDED ? "success" : "failure")
                            + " result " + String.valueOf(result));
                    success = result == PackageUtils.INSTALL_SUCCEEDED;
                }
                if (!success) {
                    break;
                }
            }
        } else if (!overwrite) {
            success = true;
        }
        Log.d(LOG_TAG, "copy and install apks " + (success ? "success" : "failure"));
        return success;
    }

    private String copyApk(Context context, String apkName, boolean overwrite) {
        File file = new File(context.getExternalFilesDir("apks"), apkName);
        String absolutePath = file.getAbsolutePath();
        try {
            InputStream is = context.getAssets().open(apkName);
            if (overwrite || !file.exists()) {
                file.deleteOnExit();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] temp = new byte[1024];
                int i = 0;
                while ((i = is.read(temp)) > 0) {
                    fos.write(temp, 0, i);
                }
                fos.close();
                is.close();
                ShellUtils.execCommand("chmod 666 " + absolutePath, true);
                Log.d(LOG_TAG, apkName + " copyed to " + absolutePath);
            } else {
                Log.d(LOG_TAG, absolutePath + " file exist skip copy ");
            }
            return absolutePath;
        } catch (IOException e) {
            Log.d(LOG_TAG, "copyApk " + apkName + " to path " + absolutePath + " error:" + String.valueOf(e.getMessage()));
        }
        return null;
    }

    private boolean resetwx() {
        ShellUtils.CommandResult result = ShellUtils.execCommand("am force-stop " + WX_PACKAGE, true);
        Log.d(LOG_TAG, "resetwx am force-stop \nresult:" + result.successMsg + "\nerror:" + result.errorMsg);
        result = ShellUtils.execCommand("pm clear " + WX_PACKAGE, true);
        Log.d(LOG_TAG, "resetwx pm clear \nresult:" + result.successMsg + "\nerror:" + result.errorMsg);
        return false;
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

}
