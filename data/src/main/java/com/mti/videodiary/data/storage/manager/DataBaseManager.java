package com.mti.videodiary.data.storage.manager;

import android.content.Context;

import com.mti.videodiary.data.storage.DataBaseHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Taras Matolinets on 27.02.15.
 */
@Singleton
public class DataBaseManager {

    private DataBaseManager instanceDataManager;
    private Context mContext;
    private NoteDataManager instanceNoteData;
    private VideoDataManager instanceVideoData;

    private DataBaseHelper mHelper;

    @Inject
    public DataBaseManager(Context ctx) {
        mContext = ctx;
        mHelper = new DataBaseHelper(ctx);
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

    private DataBaseHelper getHelper() {
        return mHelper;
    }

    public enum DataManager {
        VIDEO_MANAGER, NOTE_MANAGER;
    }

}
