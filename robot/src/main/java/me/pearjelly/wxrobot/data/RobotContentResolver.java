package me.pearjelly.wxrobot.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import me.pearjelly.wxrobot.net.pojo.DeviceInfo;

/**
 * Created by xiaobinghan on 16/4/17.
 */
public class RobotContentResolver {
    public static final String ACCOUNTS_URI = "content://" + RobotContentProvider.class.getName() + "/account";
    ContentResolver contentResolver;

    public RobotContentResolver(Context context) {
        contentResolver = context.getContentResolver();
    }

    public Uri insertAccount(DeviceInfo deviceInfo) {
        ContentValues values = new ContentValues();
        String number = deviceInfo.getPhonenumber();
        if (number.startsWith("+86")) {
            number = number.substring(3);
        }
        values.put("mobile", number);
        values.put("password", deviceInfo.getWxpasswd());
        values.put("deviceid", deviceInfo.getSerial());
        values.put("imei", deviceInfo.getImei());
        values.put("macaddr", deviceInfo.getWifimac());
        return contentResolver.insert(Uri.parse(ACCOUNTS_URI), values);
    }

    public int clear() {
        int count = contentResolver.delete(Uri.parse(ACCOUNTS_URI), null, null);
        return count;
    }

    public int updateAccount(int id, DeviceInfo deviceInfo) {
        ContentValues values = new ContentValues();
        String number = deviceInfo.getPhonenumber();
        if (number.startsWith("+86")) {
            number = number.substring(3);
        }
        values.put("mobile", number);
        values.put("password", deviceInfo.getWxpasswd());
        values.put("deviceid", deviceInfo.getSerial());
        values.put("imei", deviceInfo.getImei());
        values.put("macaddr", deviceInfo.getWifimac());
        return contentResolver.update(Uri.parse(ACCOUNTS_URI + "/" + id), values, null, null);
    }
}
