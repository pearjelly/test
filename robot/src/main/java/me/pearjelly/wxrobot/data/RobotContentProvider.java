package me.pearjelly.wxrobot.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by xiaobinghan on 16/4/17.
 */
public class RobotContentProvider extends ContentProvider {
    private DBOpenHelper dbOpenHelper;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ACCOUNTS = 1;
    private static final int ACCOUNT = 2;

    static {
        MATCHER.addURI(RobotContentProvider.class.getName(), "account", ACCOUNTS);
        MATCHER.addURI(RobotContentProvider.class.getName(), "account/#", ACCOUNT);
    }

    @Override
    public boolean onCreate() {
        this.dbOpenHelper = new DBOpenHelper(this.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        switch (MATCHER.match(uri)) {
            case ACCOUNTS:
                return db.query("account", projection, selection, selectionArgs, null, null, sortOrder);
            case ACCOUNT:
                long id = ContentUris.parseId(uri);
                String where = "id = " + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                return db.query("account", projection, where, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case ACCOUNTS:
                return "vnd.android.cursor.dir/accounts";

            case ACCOUNT:
                return "vnd.android.cursor.item/account";

            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case ACCOUNTS:
                long rowid = db.insert("account", "mobile", values);
                Uri insertUri = ContentUris.withAppendedId(uri, rowid);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return insertUri;

            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int count = 0;
        switch (MATCHER.match(uri)) {
            case ACCOUNTS:
                count = db.delete("account", selection, selectionArgs);
                return count;
            case ACCOUNT:
                long id = ContentUris.parseId(uri);
                String where = "id = " + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.delete("account", where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int count = 0;
        switch (MATCHER.match(uri)) {
            case ACCOUNTS:
                count = db.update("account", values, selection, selectionArgs);
                return count;
            case ACCOUNT:
                long id = ContentUris.parseId(uri);
                String where = "id = " + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.update("account", values, where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }
}
