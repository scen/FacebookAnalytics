package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by scen on 8/28/13.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME_PREFIX = "fbanalytics_";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_KEYVAL = "keyval";

    private static final String KEY_KEY = "key";
    private static final String KEY_VAL = "val";

    public DatabaseHandler(Context context) {
        super(context, getDBName(), null, DATABASE_VERSION);
    }

    private static String getDBName() {
        return DATABASE_NAME_PREFIX + GlobalApp.get().fb.me.getId() + ".db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_KEYVAL + "(" + KEY_KEY + " TEXT," + KEY_VAL + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
