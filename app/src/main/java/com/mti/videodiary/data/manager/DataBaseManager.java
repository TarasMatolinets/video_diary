package com.mti.videodiary.data.manager;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.mti.videodiary.data.DataBaseHelper;
import com.mti.videodiary.data.dao.Video;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Taras Matolinets on 27.02.15.
 */
public class DataBaseManager {

    static private DataBaseManager instanceDataManager;
    private Context mContext;
    private static NoteDataManager instanceNoteData;
    private static VideoDataManager instanceVideoData;

    protected static DataBaseHelper mHelper;

    public DataBaseManager() {}

    public DataBaseManager(Context ctx) {
        mContext = ctx;
        mHelper = new DataBaseHelper(ctx);
    }

    static public void init(Context ctx) {
        if (null == instanceDataManager) {
            instanceDataManager = new DataBaseManager(ctx);
        }
    }

    public DataBaseManager getCurrentManager(DataManager manager) {
        switch (manager) {
            case NOTE_MANAGER:
                return instanceNoteData = getNoteManager(mContext);
            case VIDEO_MANAGER:
                return instanceVideoData = getVideoManager(mContext);

            default:
                return instanceDataManager;
        }

    }

    private NoteDataManager getNoteManager(Context ctx) {
        if (null == instanceNoteData) {
            return instanceNoteData = new NoteDataManager(ctx);
        } else
            return instanceNoteData;
    }

    private VideoDataManager getVideoManager(Context ctx) {
        if (null == instanceVideoData) {
            return instanceVideoData = new VideoDataManager(ctx);
        } else
            return instanceVideoData;
    }


    static public DataBaseManager getInstanceDataManager() {
        return instanceDataManager;
    }

    private DataBaseHelper getHelper() {
        return mHelper;
    }

    public enum DataManager {
        VIDEO_MANAGER, NOTE_MANAGER;
    }

}
