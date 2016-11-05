package com.mti.videodiary.data.storage.manager;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.mti.videodiary.data.storage.dao.Video;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Taras Matolinets on 29.03.15.
 */
public class VideoDataManager extends DataBaseManager{
    private Context mContext;

    public VideoDataManager(Context ctx) {
        mContext = ctx;
    }

    public List<Video> getAllVideosList() {
        List<Video> videoList = null;
        try {
            videoList = mHelper.getVideoListDao().queryForAll();

            Collections.reverse(videoList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videoList;
    }

    public void deleteVideosList() {
        List<Video> videoList;
        try {
            Dao<Video, Integer> daoVideoList = mHelper.getVideoListDao();
            videoList = daoVideoList.queryForAll();

            Collections.reverse(videoList);

            daoVideoList.delete(videoList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVideoById(int id) {
        try {
            mHelper.getVideoListDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Video getVideoByPosition(int id) {
        Video video = null;
        try {
            List<Video> videoList = mHelper.getVideoListDao().queryForAll();
            Collections.reverse(videoList);

            video = videoList.get(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return video;
    }


    public void createVideo(Video video) {
        try {
            mHelper.getVideoListDao().create(video);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVideoList(Video video) {
        try {
            mHelper.getVideoListDao().update(video);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
