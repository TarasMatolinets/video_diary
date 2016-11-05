package com.mti.videodiary.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.storage.dao.Video;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.mti.videodiary.data.Constants.TAG;

/**
 * Created by Taras Matolinets on 26.02.15.
 */
@Singleton
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "video_dairy_db";
    private static final int DB_VERSION = 1;

    private Context mContext;
    private Dao<Video, Integer> listVideos = null;
    private Dao<Note, Integer> listNotes = null;

    @Inject
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");

            TableUtils.createTable(connectionSource, Video.class);
            TableUtils.createTable(connectionSource, Note.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            Log.i(TAG, "onUpgrade");

            TableUtils.dropTable(connectionSource, Video.class, true);
            TableUtils.dropTable(connectionSource, Note.class, true);

            onCreate(sqLiteDatabase, connectionSource);

        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Video, Integer> getVideoListDao() {
        if (null == listVideos) {
            try {
                listVideos = getDao(Video.class);
            } catch (SQLException e) {
                Log.e(TAG, e.toString());
            }
        }
        return listVideos;
    }

    public Dao<Note, Integer> getNoteListDao() {
        if (null == listNotes) {
            try {
                listNotes = getDao(Note.class);
            } catch (SQLException e) {
                Log.e(TAG, e.toString());
            }
        }
        return listNotes;
    }

}
