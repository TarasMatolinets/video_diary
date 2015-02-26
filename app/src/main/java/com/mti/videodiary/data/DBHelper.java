package com.mti.videodiary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Taras Matolinets on 26.02.15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "video_daily_db";
    private static final int DB_VERSION = 1;

    private static final String DB_TABLE_VIDEOS = "videos";
    private static final String DB_TABLE_NOTES = "notes";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_VIDEO = "video";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String DB_CREATE_VIDEO_TABLE = "create table "+ DB_NAME + "("+
            COLUMN_ID + " integer primary key autoincrement, "+
            COLUMN_VIDEO + " text, " + COLUMN_TITLE + "text, "+COLUMN_DESCRIPTION +"text"+");";

    private Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        mContext = context;
    }

    public void open() {
        mDBHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_VIDEO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
