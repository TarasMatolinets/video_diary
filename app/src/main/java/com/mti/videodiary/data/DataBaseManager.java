package com.mti.videodiary.data;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.mti.videodiary.data.dao.Video;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Taras Matolinets on 27.02.15.
 */
public class DataBaseManager {

    static private DataBaseManager instance;
    private DataBaseHelper mHelper;

    public DataBaseManager(Context ctx) {
        mHelper = new DataBaseHelper(ctx);
    }

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new DataBaseManager(ctx);
        }
    }

    static public DataBaseManager getInstance() {
        return instance;
    }

    private DataBaseHelper getHelper() {
        return mHelper;
    }

    public List<Video> getAllVideosList() {
        List<Video> videoList = null;
        try {
            videoList = getHelper().getVideoListDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videoList;
    }

    public void deleteVideosList() {
        List<Video> videoList;
        try {
            Dao<Video, Integer> daoVideoList = getHelper().getVideoListDao();
            videoList = daoVideoList.queryForAll();
            daoVideoList.delete(videoList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVideoById(int id) {
        try {
            getHelper().getVideoListDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Video getVideoById(int wishListId) {
        Video video = null;
        try {
            video = getHelper().getVideoListDao().queryForId(wishListId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return video;
    }

    public void createVideo(Video video) {
        try {
            getHelper().getVideoListDao().create(video);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVideoList(Video video) {
        try {
            getHelper().getVideoListDao().update(video);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
