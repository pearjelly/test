package me.pearjelly.wxrobot.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaobinghan on 16/4/17.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "wxrobot.db";
    private static final int DBVER = 1;

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, DBVER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE account (id integer primary key autoincrement, deviceid varchar(20), imei varchar(20), macaddr varchar(20), mobile varchar(20), password varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS account");
        onCreate(db);
    }
}
