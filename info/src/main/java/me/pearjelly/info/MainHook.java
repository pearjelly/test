package me.pearjelly.info;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by xiaobinghan on 16/4/6.
 */
public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        if (lpparam.packageName.startsWith("com.tencent.mm") || lpparam.packageName.startsWith("me.pearjelly.")) {
            XposedBridge.log("hooking " + lpparam.packageName);
            XposedHelpers.findAndHookMethod(android.app.Activity.class, "getSystemService", new Object[]{String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("pg:" + String.valueOf(lpparam.packageName) + " call getSystemService param:" + String.valueOf(param.args[0]));
                }
            }});
            try {
                XSharedPreferences pre = new XSharedPreferences("me.pearjelly.info", "prefs");
                String ks[] = {"imei", "imsi", "number","netcountryiso","simcountryiso", "simserial",
                        "wifimac", "bluemac", "androidid", "serial", "brand",  "manufacturer", "model"};
                HashMap<String, String> maps = new HashMap<String, String>();
                for (String k : ks) {
                    String v = pre.getString(k, null);
                    if (TextUtils.isEmpty(v)) {
                        XposedBridge.log(lpparam.packageName + " hook key: " + k + " value is null");
                    } else {
                        maps.put(k, v);
                    }
                }
                if (maps.isEmpty()) {
                    XposedBridge.log(lpparam.packageName + " no hook key");
                } else {
                    hookAll(lpparam.packageName, maps);
                }
            } catch (Throwable e) {
                XposedBridge.log(lpparam.packageName + " get hook keys error:" + e.getMessage());
            }
        }
    }

    private void hookAll(final String pg, final Map<String, String> map) {
        String imei = map.get("imei");
        if (!TextUtils.isEmpty(imei)) {
            hookMethod(TelephonyManager.class, "getDeviceId", imei, pg);
        }
        String imsi = map.get("imsi");
        if (!TextUtils.isEmpty(imsi)) {
            hookMethod(TelephonyManager.class, "getSubscriberId", imsi, pg);
        }
        String number = map.get("number");
        if (!TextUtils.isEmpty(number)) {
            hookMethod(TelephonyManager.class, "getLine1Number", number, pg);
        }
        String netcountryiso = map.get("netcountryiso");
        if (!TextUtils.isEmpty(netcountryiso)) {
            hookMethod(TelephonyManager.class, "getNetworkCountryIso", netcountryiso, pg);
        }
        String simcountryiso = map.get("simcountryiso");
        if (!TextUtils.isEmpty(simcountryiso)) {
            hookMethod(TelephonyManager.class, "getSimCountryIso", simcountryiso, pg);
        }
        String simserial = map.get("simserial");
        if (!TextUtils.isEmpty(simserial)) {
            hookMethod(TelephonyManager.class, "getSimSerialNumber",
                    simserial, pg);
        }
        String wifimac = map.get("wifimac");
        if (!TextUtils.isEmpty(wifimac)) {
            hookMethod(WifiInfo.class, "getMacAddress", wifimac, pg);
        }
        String bluemac = map.get("bluemac");
        if (!TextUtils.isEmpty(bluemac)) {
            hookMethod(BluetoothAdapter.class, "getAddress", bluemac, pg);
        }
        try {
            String serial = map.get("serial");
            if (!TextUtils.isEmpty(serial)) {
                XposedHelpers.findField(android.os.Build.class, "SERIAL").set(null, serial);
                XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod serial:" + String.valueOf(serial));
            }
            String brand = map.get("brand");
            if (!TextUtils.isEmpty(brand)) {
                XposedHelpers.findField(android.os.Build.class, "BRAND").set(null, brand);
                XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod brand:" + String.valueOf(brand));
            }
            String manufacturer = map.get("manufacturer");
            if (!TextUtils.isEmpty(manufacturer)) {
                XposedHelpers.findField(android.os.Build.class, "MANUFACTURER").set(null, manufacturer);
                XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod manufacturer:" + String.valueOf(manufacturer));
            }
            String model = map.get("model");
            if (!TextUtils.isEmpty(model)) {
                XposedHelpers.findField(android.os.Build.class, "MODEL").set(null, model);
                XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod model:" + String.valueOf(model));
            }
        } catch (Throwable e) {
            XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod serial or brand or model error:" + e.getMessage());
        }
        try {
            final String androidid = map.get("androidid");
            if (!TextUtils.isEmpty(androidid)) {
                XposedHelpers.findAndHookMethod(
                        android.provider.Settings.Secure.class, "getString",
                        new Object[]{ContentResolver.class, String.class,
                                new XC_MethodHook() {
                                    protected void afterHookedMethod(
                                            MethodHookParam param) throws Throwable {
                                        if (param.args[1] == "android_id") {
                                            param.setResult(androidid);
                                            XposedBridge.log("pg:" + String.valueOf(pg) + " hookedMethod Secure.getString result:" + String.valueOf(androidid));
                                        }
                                    }
                                }});
            }
            XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod androidid result:" + String.valueOf(androidid));
        } catch (Throwable e) {
            XposedBridge.log("pg:" + String.valueOf(pg) + " hook androidid error:" + e.getMessage());
        }
    }

    private void hookMethod(final Class cl, final String method,
                            final String result, final String pg) {
        try {
            XposedHelpers.findAndHookMethod(cl, method,
                    new Object[]{new XC_MethodHook() {
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            param.setResult(result);
                            XposedBridge.log("pg:" + String.valueOf(pg) + " hookedMethod " + method + " result:" + String.valueOf(result));
                        }

                    }});
            XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod " + method + " result:" + String.valueOf(result));
        } catch (Throwable e) {
            XposedBridge.log("pg:" + String.valueOf(pg) + " hookingMethod " + method + " failure !" + e.getMessage());
        }
    }
}
