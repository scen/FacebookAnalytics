package com.stanleycen.facebookanalytics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by scen on 8/28/13.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME_PREFIX = "fbanalytics_";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_KEYVAL = "keyval";
    public static final String TABLE_ATTACHMENTS = "attachments";
    public static final String TABLE_THREADS = "threads";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VAL = "val";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WIDTH = "width";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_MIMETYPE = "mimetype";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_PREVIEWURL = "previewurl";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_THREAD = "thread";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ISGROUP = "isgroup";
    public static final String COLUMN_PARTICIPANTS = "participants";
//    private static final String COLUMN_MESSAGES = "messages";
    public static final String COLUMN_MESSAGECOUNT = "messagecount";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_FROM = "_from";
    public static final String COLUMN_HASCOORD = "hascoord";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ATTACHMENTS = "attachments";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SOURCE = "source";

    private static final String CREATE_TABLE_KEYVAL =
            "CREATE TABLE " + TABLE_KEYVAL + "(" + COLUMN_KEY + " TEXT," + COLUMN_VAL + " TEXT)";
    private static final String CREATE_TABLE_ATTACHMENTS =
            "CREATE TABLE " + TABLE_ATTACHMENTS + "(" + COLUMN_ID + " TEXT," + COLUMN_WIDTH + " INTEGER," +
            COLUMN_HEIGHT + " INTEGER," + COLUMN_URL + " TEXT," + COLUMN_PREVIEWURL + " TEXT," +
            COLUMN_MIMETYPE + " TEXT," + COLUMN_TYPE + " INTEGER," + COLUMN_MESSAGE + " TEXT," + COLUMN_THREAD + " TEXT)";
    private static final String CREATE_TABLE_THREADS =
            "CREATE TABLE " + TABLE_THREADS + "(" + COLUMN_ID + " TEXT," + COLUMN_TITLE + " TEXT," +
            COLUMN_TIMESTAMP + " INTEGER," + COLUMN_ISGROUP + " INTEGER," + COLUMN_PARTICIPANTS + " TEXT," +
            COLUMN_MESSAGECOUNT + " INTEGER)";
    private static final String CREATE_TABLE_MESSAGES =
            "CREATE TABLE " + TABLE_MESSAGES + "(" + COLUMN_ID + " TEXT," + COLUMN_FROM + " TEXT," + COLUMN_TIMESTAMP + " INTEGER," +
            COLUMN_BODY + " TEXT," + COLUMN_THREAD + " TEXT," + COLUMN_HASCOORD + " INTEGER," + COLUMN_LATITUDE + " REAL," +
            COLUMN_LONGITUDE + " REAL," + COLUMN_ATTACHMENTS + " TEXT," + COLUMN_SOURCE + " INTEGER)";
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" + COLUMN_ID + " TEXT," + COLUMN_NAME + " TEXT)";

    private static final String TAG = "DBHandler";

    public DatabaseHandler(Context context) {
        super(context, getDBName(), null, DATABASE_VERSION);
    }

    private static String getDBName() {
        return DATABASE_NAME_PREFIX + GlobalApp.get().fb.me.getId() + ".db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KEYVAL);
        db.execSQL(CREATE_TABLE_ATTACHMENTS);
        db.execSQL(CREATE_TABLE_THREADS);
        db.execSQL(CREATE_TABLE_MESSAGES);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database " + oldVersion + " -> " + newVersion);
        dropAllTables(db);
        onCreate(db);
    }

    public void dropAllTables(SQLiteDatabase db) {
        dropTableIfExists(db, TABLE_KEYVAL);
        dropTableIfExists(db, TABLE_ATTACHMENTS);
        dropTableIfExists(db, TABLE_THREADS);
        dropTableIfExists(db, TABLE_MESSAGES);
        dropTableIfExists(db, TABLE_USERS);
    }

    public void clearAllTables(SQLiteDatabase db) {
        db.delete(TABLE_KEYVAL, null, null);
        db.delete(TABLE_ATTACHMENTS, null, null);
        db.delete(TABLE_THREADS, null, null);
        db.delete(TABLE_MESSAGES, null, null);
        db.delete(TABLE_USERS, null, null);
    }

    public void insertKeyVal(SQLiteDatabase db, String key, String val) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_KEY, key);
        cv.put(COLUMN_VAL, val);
        db.insert(TABLE_KEYVAL, null, cv);
    }

    public String getKeyVal(String key) {
        Cursor c = getReadableDatabase().rawQuery("SELECT " + COLUMN_VAL + " FROM " + TABLE_KEYVAL
                + " WHERE " + COLUMN_KEY + "=?", new String[]{key});
        if (c.getCount() == 0) return null;
        c.moveToFirst();
        return c.getString(0);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, 1, 2);
    }

    private void dropTableIfExists(SQLiteDatabase db, String name) {
        Log.w(TAG, "Dropping table " + name);
        db.execSQL("DROP TABLE IF EXISTS " + name);
    }
}
