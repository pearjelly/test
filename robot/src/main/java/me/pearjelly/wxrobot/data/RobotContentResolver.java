package me.pearjelly.wxrobot.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import me.pearjelly.wxrobot.data.pojo.AccountInfo;

/**
 * Created by xiaobinghan on 16/4/17.
 */
public class RobotContentResolver {
    public static final String ACCOUNTS_URI = "content://" + RobotContentProvider.class.getName() + "/account";
    ContentResolver contentResolver;

    public RobotContentResolver(Context context) {
        contentResolver = context.getContentResolver();
    }

    public Uri insertAccount(AccountInfo accountInfo) {
        ContentValues values = new ContentValues();
        values.put("mobile", accountInfo.getNumber());
        values.put("password", accountInfo.getWxpasswd());
        values.put("deviceid", accountInfo.getSerial());
        values.put("imei", accountInfo.getImei());
        values.put("macaddr", accountInfo.getMacaddr());
        return contentResolver.insert(Uri.parse(ACCOUNTS_URI), values);
    }

    public int updateAccount(int id, AccountInfo accountInfo) {
        ContentValues values = new ContentValues();
        values.put("mobile", accountInfo.getNumber());
        values.put("password", accountInfo.getWxpasswd());
        values.put("deviceid", accountInfo.getSerial());
        values.put("imei", accountInfo.getImei());
        values.put("macaddr", accountInfo.getMacaddr());
        return contentResolver.update(Uri.parse(ACCOUNTS_URI + "/" + id), values, null, null);
    }
}
