package me.pearjelly.wxrobot.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import me.pearjelly.wxrobot.data.pojo.AccountInfo;

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

    public WorkerService() {
        super();
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
        Context context = getApplicationContext();
        boolean initApks = initApks(context, true);
        RobotContentResolver contentResolver = new RobotContentResolver(context);
        if (initApks) {
            int count = 0;
            AccountInfo[] accountInfos = new AccountInfo[]{
                    new AccountInfo(4, "d665d019", "867345020000403", "74:04:2b:d1:0f:c7", "00:00:00:FD:E2:26", "f8e95b3d3f495da8", "qcom", "qcom", "wt86528_C2", "15550419396", "460014115421626", "89860114451701435801", "920e61de"),
                    new AccountInfo(4, "d665d019", "867345020000403", "74:04:2b:d1:0f:c7", "00:00:00:FD:E2:26", "f8e95b3d3f495da8", "qcom", "qcom", "wt86528_C2", "15550419396", "460014115421626", "89860114451701435801", "920e61de"),
                    new AccountInfo(1, "4d00cd64b490a0e1", "357070058816663", "34:23:BA:B7:9A:72", "CC:07:AB:2A:78:E9", "eab6e1139cc479d4", "samsung", "samsung", "GT-I9300", "13161294255", "963764816161647", "75763419623121926899", "tiantian")
            };
            while (true) {
                try {
                    count++;
                    AccountInfo accountInfo = accountInfos[count % 2];
                    String mobile = accountInfo.getNumber();
                    contentResolver.insertAccount(accountInfo);
                    Context infoContext = null;
                    try {
                        infoContext = createPackageContext("me.pearjelly.info", Context.CONTEXT_IGNORE_SECURITY);
                        SharedPreferences preferences = infoContext.getSharedPreferences("prefs", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                        Log.d(LOG_TAG, "get me.pearjelly.info preferences:" + String.valueOf(preferences));
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("imei", accountInfo.getImei());
                        edit.putString("wifimac", accountInfo.getMacaddr());
                        edit.putString("bluemac", accountInfo.getBluemac());
                        edit.putString("androidid", accountInfo.getAndroidid());
                        edit.putString("serial", accountInfo.getSerial());
                        edit.putString("brand", accountInfo.getBrand());
                        edit.putString("manufacturer", accountInfo.getManufacturer());
                        edit.putString("model", accountInfo.getModel());
                        edit.putString("number", mobile.startsWith("+") ? mobile : "+86" + mobile);
                        edit.putString("imsi", accountInfo.getImsi());
                        edit.putString("simserial", accountInfo.getSimserial());
                        edit.apply();
                        Log.d(LOG_TAG, "write account info:" + String.valueOf(accountInfo));
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.d(LOG_TAG, "createPackageContext error " + String.valueOf(e.getMessage()));
                    }

                    resetwx();
                    ShellUtils.CommandResult result = ShellUtils.execCommand("am instrument -w -r -e debug false -e class com.tencent.mm.test.LoginWithMobile com.tencent.mm.test/android.test.InstrumentationTestRunner", true);
                    Log.d(LOG_TAG, "exec command mobile:" + mobile + " count:" + count + "\nresult:" + result.successMsg + "\nerror:" + result.errorMsg);
                    Log.d(LOG_TAG, "我还在运行..");
                    Thread.currentThread().sleep((long) (Math.random() * 3 * 60 * 1000));
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }
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
