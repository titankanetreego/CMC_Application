package com.example.cmc_application.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cmc_application.model.SettingsModel;

import java.util.ArrayList;

public class SettingsDataModule extends SQLiteOpenHelper {
    private static final String LOG_TAG = "SettingsDataModule";

    // db configuration0
    private final static String TABLE_NAME = "settings_data";

    // db table column name
    private final static String KEY_ID = "_id";
    private final static String KEY_KEY = "_key";
    private final static String KEY_TIME = "_time";
    private final static String KEY_VOLTAGE = "_voltage";
    private final static String KEY_CURRENT = "_current";
    private final static String KEY_YEAR = "_year";
    private final static String KEY_MONTH = "_month";
    private final static String KEY_SERIAL = "_serial";

    // db table column index
    private final static int TABLE_COLUMN_ID = 0;
    private final static int TABLE_COLUMN_KEY = 1;
    private final static int TABLE_COLUMN_TIME = 2;
    private final static int TABLE_COLUMN_VOLTAGE = 3;
    private final static int TABLE_COLUMN_CURRENT = 4;
    private final static int TABLE_COLUMN_YEAR = 5;
    private final static int TABLE_COLUMN_MONTH = 6;
    private final static int TABLE_COLUMN_SERIAL = 7;

    // clear and reset db for debuging
    private final static boolean RESET_DB = false;

    public SettingsDataModule(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_KEY + " INTEGER NOT NULL, "
                + KEY_TIME + " TEXT NOT NULL, "
                + KEY_VOLTAGE + " INTEGER NOT NULL, "
                + KEY_CURRENT + " TEXT NOT NULL, "
                + KEY_YEAR + " INTEGER NOT NULL, "
                + KEY_MONTH + " INTEGER NOT NULL, "
                + KEY_SERIAL + " TEXT NOT NULL"
                + ");";
        Log.d(LOG_TAG, "onCreate sql: " + sql);

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String sql = "DROP TABLE " + TABLE_NAME;
        Log.d(LOG_TAG, "onUpgrade sql: " + sql);
        db.execSQL(sql);
        onCreate(db);
    }

    synchronized public long insert(SettingsModel data) {
        SQLiteDatabase db = getWritableDatabase();
        if (RESET_DB)
            onUpgrade(db, 0, 1);

        ContentValues values = new ContentValues();
        values.put(KEY_KEY, data.key);
        values.put(KEY_TIME, data.time);
        values.put(KEY_VOLTAGE, data.voltage);
        values.put(KEY_CURRENT, data.current);
        values.put(KEY_YEAR, data.year);
        values.put(KEY_MONTH, data.month);
        values.put(KEY_SERIAL, data.serial);
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    synchronized public SettingsModel fetchFirstByKey(String key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, new String[] { KEY_ID, KEY_KEY, KEY_TIME, KEY_VOLTAGE, KEY_CURRENT, KEY_YEAR, KEY_MONTH, KEY_SERIAL},
                KEY_KEY + "=\"" + key + "\"", null, null, null, null);
        if (c == null || c.getCount() <= 0) {
            closeDb(db, c);
            return null;
        }

        try {
            c.moveToFirst();
        } catch(Exception e) {
            e.printStackTrace();
            closeDb(db, c);
            return null;
        }

        SettingsModel data = new SettingsModel(c.getInt(TABLE_COLUMN_ID), c.getString(TABLE_COLUMN_KEY), c.getString(TABLE_COLUMN_TIME), c.getString(TABLE_COLUMN_VOLTAGE),
                c.getString(TABLE_COLUMN_CURRENT), c.getString(TABLE_COLUMN_YEAR), c.getString(TABLE_COLUMN_MONTH), c.getString(TABLE_COLUMN_SERIAL));
        closeDb(db, c);
        return data;
    }


    synchronized public int deleteByKey(String key) {
        SQLiteDatabase db = getReadableDatabase();
        int ret = db.delete(TABLE_NAME, KEY_KEY + "=\"" + key + "\"", null);
        db.close();
        return ret;
    }

    synchronized public long update(SettingsModel data) {
        deleteByKey(data.key);
        return insert(data);
    }

    private void closeDb(SQLiteDatabase db, Cursor cursor) {
        if (cursor != null)
            cursor.close();

        if (db != null)
            db.close();
    }

    synchronized public int deleteAll() {
        SQLiteDatabase db = getReadableDatabase();
        int ret = db.delete(TABLE_NAME, null, null);
        db.close();
        return ret;
    }
}
