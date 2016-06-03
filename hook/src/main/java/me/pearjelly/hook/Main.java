package me.pearjelly.hook;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.saurik.substrate.MS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by hxb on 2016/5/18.
 */
public class Main {

    public static final String LOG_TAG = Main.class.getName();
    static String ks[] = {
            "imei"
            , "imsi"
            , "number"
            , "netcountryiso"
            , "simcountryiso"
            , "simserial"
            , "simoperator"
            , "simoperatorname"
            , "wifimac"
            , "bluemac"

            , "androidid"

            , "serial"
            , "brand"
            , "manufacturer"
            , "model"


            //error
            , "voicecapable"
            , "phonetype"
            , "simstate"
    };
    static Map<String, Object> methodKeyMap = new HashMap<>();

    static void initialize() throws NoSuchFieldException {
        try {
            methodKeyMap.put("imsi", TelephonyManager.class.getMethod("getSubscriberId"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getSubscriberId failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("number", TelephonyManager.class.getMethod("getLine1Number"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getLine1Number failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("netcountryiso", TelephonyManager.class.getMethod("getNetworkCountryIso"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getNetworkCountryIso failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("simcountryiso", TelephonyManager.class.getMethod("getSimCountryIso"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getSimCountryIso failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("simserial", TelephonyManager.class.getMethod("getSimSerialNumber"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getSimSerialNumber failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("simoperator", TelephonyManager.class.getMethod("getSimOperator"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getSimOperator failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("simoperatorname", TelephonyManager.class.getMethod("getSimOperatorName"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getSimOperatorName failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("imei", TelephonyManager.class.getMethod("getDeviceId"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getDeviceId failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("wifimac", WifiInfo.class.getMethod("getMacAddress"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getMacAddress failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("bluemac", BluetoothAdapter.class.getMethod("getAddress"));
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, " hookingMethod getAddress failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("serial", Build.class.getDeclaredField("SERIAL"));
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, " hookingField MANUFACTURER failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("brand", Build.class.getDeclaredField("BRAND"));
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, " hookingField MANUFACTURER failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("manufacturer", Build.class.getDeclaredField("MANUFACTURER"));
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, " hookingField MANUFACTURER failed exception:" + String.valueOf(e));
        }
        try {
            methodKeyMap.put("model", Build.class.getDeclaredField("MODEL"));
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, " hookingField MANUFACTURER failed2 exception:" + String.valueOf(e));
        }
//        try {
//            methodKeyMap.put("androidid", Settings.Secure.class.getMethod("getString", ContentResolver.class, String.class));
//        } catch (NoSuchMethodException e) {
//            Log.i(LOG_TAG, " hookingMethod getString failed exception:" + String.valueOf(e));
//        }
//        try {
//            methodKeyMap.put("simstate", TelephonyManager.class.getMethod("getSimState"));
//        } catch (NoSuchMethodException e) {
//            Log.i(LOG_TAG, " hookingMethod getSimState failed exception:" + String.valueOf(e));
//        }
//        try {
//            methodKeyMap.put("voicecapable", TelephonyManager.class.getMethod("isVoiceCapable"));
//        } catch (NoSuchMethodException e) {
//            Log.i(LOG_TAG, " hookingMethod isVoiceCapable failed exception:" + String.valueOf(e));
//        }
//        try {
//            methodKeyMap.put("phonetype", TelephonyManager.class.getMethod("getPhoneType"));
//        } catch (NoSuchMethodException e) {
//            Log.i(LOG_TAG, " hookingMethod getPhoneType failed exception:" + String.valueOf(e));
//        }

        for (final Class<?> clazz : new Class<?>[]{BluetoothAdapter.class, WifiInfo.class, TelephonyManager.class, Settings.Secure.class, Build.class}) {
            MS.hookClassLoad(clazz.getName(), new MS.ClassLoadHook() {
                public void classLoaded(Class<?> _clazz) {
                    Log.i(LOG_TAG, " classLoaded " + String.valueOf(_clazz) + " classLoader:" + String.valueOf(clazz.getClassLoader()));
                    XSharedPreferences pre = new XSharedPreferences("me.pearjelly.info", "prefs");
                    for (String key : methodKeyMap.keySet()) {
                        Object o = methodKeyMap.get(key);
                        if (o instanceof Method) {
                            Method method = (Method) o;
                            String value = pre.getString(key, null);
                            hookMethod(_clazz, method, value);
                        } else if (o instanceof Field) {
                            Field field = (Field) o;
                            String value = pre.getString(key, null);
                            hookField(_clazz, field, value);
                        }
                    }
                }
            });
        }
    }

    private static void hookField(Class<?> clazz, final Field field, final String hookingValue) {
        if (field.getDeclaringClass().equals(clazz) && hookingValue != null) {
//            MS.moveUnderClassLoader(clazz.getClassLoader(), new Runnable() {
//                @Override
//                public void run() {
                    field.setAccessible(true);
                    try {
                        Field modifiersField = Field.class.getField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        field.set(null, hookingValue);
                        Log.i(LOG_TAG, " hookedField " + field.getName() + " result:" + String.valueOf(hookingValue));
                    } catch (Exception e) {
                        Log.e(LOG_TAG, " hookingField " + field.getName() + " failed exception:" + String.valueOf(e));
                    }
//                }
//            });
        } else {
            Log.d(LOG_TAG, " hookingField " + field.getName() + " failed");
        }
    }

    private static void hookMethod(Class<?> clazz, final Method method, final Object hookingValue) {
        if (method.getDeclaringClass().equals(clazz) && hookingValue != null) {
            MS.hookMethod(clazz, method, new MS.MethodAlteration<Object, Object>() {
                @Override
                public Object invoked(Object _this, Object... args) throws Throwable {
                    if (hookingValue == null || (_this.getClass().equals(Settings.Secure.class) && args != null && args.length > 1 && !"android_id".equals(args[1]))) {
                        Object _value = invoke(_this, args);
                        Log.i(LOG_TAG, " hookingMethod " + method.getName() + " failed result:" + String.valueOf(_value));
                        return _value;
                    } else {
                        Log.i(LOG_TAG, " hookedMethod " + method.getName() + " result:" + String.valueOf(hookingValue));
                        return hookingValue;
                    }
                }
            });
        } else {
            Log.d(LOG_TAG, " hookingMethod " + method.getName() + " failed");
        }
    }
}
