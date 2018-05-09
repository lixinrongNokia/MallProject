package cn.iliker.mall;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import static iliker.utils.DBHelper.getDB;

public class PushMsgProvider extends android.content.ContentProvider {
    private final static String HOST = "cn.iliker.mall.aliPushMsg";
    private static final UriMatcher uriMatcher;
    private static final int ADDMSG = 1;
    private static final int DELALL = 2;
    private static final int UPDATE = 3;
    private static final int GETUNMSG = 4;
    private static final int DEL = 5;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(HOST, "add", ADDMSG);
        uriMatcher.addURI(HOST, "delAll", DELALL);
        uriMatcher.addURI(HOST, "update", UPDATE);
        uriMatcher.addURI(HOST, "getUnMsg", GETUNMSG);
        uriMatcher.addURI(HOST, "del", DEL);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) != GETUNMSG) {
            throw new IllegalArgumentException("Error Uri: " + uri);
        }
        return getDB().getMessage(selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != ADDMSG) {
            throw new IllegalArgumentException("Error Uri: " + uri);
        }
        long id = getDB().addPushMag(values);
        if (id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == DEL) {
            return getDB().delMsgById(selectionArgs);
        } else if (uriMatcher.match(uri) == DELALL) {
            getDB().delAllMsg(selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != UPDATE) {
            throw new IllegalArgumentException("Error Uri: " + uri);
        }
        getDB().modifyMsg(values, selection, selectionArgs);
        return 0;
    }
}
