package com.mti.videodiary.data.storage.manager;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mti.videodiary.data.Constants;
import com.mti.videodiary.data.storage.DataBaseHelper;
import com.mti.videodiary.data.storage.dao.Video;
import com.mti.videodiary.data.transformer.DataToDomainTransformer;
import com.mti.videodiary.data.transformer.DomainToDataTransformer;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import database.VideoDataBase;
import model.VideoDomain;
import rx.Observable;
import rx.Subscriber;

import static com.mti.videodiary.data.Constants.FILE_FORMAT;
import static com.mti.videodiary.data.Constants.TAG;
import static com.mti.videodiary.data.Constants.VIDEO_DIR;
import static com.mti.videodiary.data.storage.dao.Video.ID;
import static java.io.File.separator;


/**
 * Created by Terry on 11/6/2016.
 * Implements {@link VideoDataBase} for communicate with database
 */

public class VideoNoteDataBaseFactory implements VideoDataBase {

    private final DataBaseHelper mHelper;

    @Inject
    public VideoNoteDataBaseFactory(DataBaseHelper helper) {
        mHelper = helper;
    }

    @Override
    public Observable<List<VideoDomain>> getListVideos() {
        return Observable.create(new Observable.OnSubscribe<List<VideoDomain>>() {
            @Override
            public void call(Subscriber<? super List<VideoDomain>> subscriber) {
                try {
                    List<Video> noteList = mHelper.getVideoListDao().queryForAll();
                    Collections.reverse(noteList);

                    DataToDomainTransformer transformer = new DataToDomainTransformer();
                    List<VideoDomain> listDomain = transformer.transformVideoList(noteList);

                    subscriber.onNext(listDomain);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<VideoDomain> getVideoById(final int id) {
        return Observable.create(new Observable.OnSubscribe<VideoDomain>() {
            @Override
            public void call(Subscriber<? super VideoDomain> subscriber) {
                QueryBuilder<Video, Integer> queryBuilder = mHelper.getVideoListDao().queryBuilder();
                try {
                    queryBuilder.where().eq(ID, id);

                    PreparedQuery<Video> preparedQuery = queryBuilder.prepare();
                    List<Video> accountList = mHelper.getVideoListDao().query(preparedQuery);

                    int defaultValue = 0;
                    Video video = accountList.get(defaultValue);
                    DataToDomainTransformer transformer = new DataToDomainTransformer();
                    VideoDomain videoDomain = transformer.transform(video);

                    subscriber.onNext(videoDomain);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> createVideo(final VideoDomain video) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    File oldFileName = new File(video.getVideoName());
                    File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_DIR + separator + video.getVideoName() + FILE_FORMAT);

                    boolean success = oldFileName.renameTo(newFileName);

                    if (success) {
                        Log.i(TAG, "video file renamed good");
                    }

                    DomainToDataTransformer transformer = new DomainToDataTransformer();
                    Video videoData = transformer.transform(video);
                    mHelper.getVideoListDao().create(videoData);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> updateVideoList(final VideoDomain video) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    File oldFileName = new File(video.getVideoName());
                    File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_DIR + separator + video.getVideoName() + FILE_FORMAT);

                    boolean success = oldFileName.renameTo(newFileName);

                    if (success) {
                        Log.i(TAG, "video file renamed good");
                    }

                    DomainToDataTransformer transformer = new DomainToDataTransformer();
                    Video videoDomain = transformer.transform(video);
                    mHelper.getVideoListDao().update(videoDomain);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> deleteItemById(final int id) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    QueryBuilder<Video, Integer> queryBuilder = mHelper.getVideoListDao().queryBuilder();
                    queryBuilder.where().eq(ID, id);

                    PreparedQuery<Video> preparedQuery = queryBuilder.prepare();
                    List<Video> accountList = mHelper.getVideoListDao().query(preparedQuery);

                    int defaultValue = 0;
                    Video video = accountList.get(defaultValue);

                    String imageUrl = video.getImageUrl();
                    String videoUrl = video.getVideoName();

                    if (TextUtils.isEmpty(imageUrl)) {
                        File file = new File(imageUrl);
                        boolean deleted = file.delete();

                        if (deleted) {
                            Log.i(Constants.TAG, "image file deleted successful");
                        }
                    }

                    if (TextUtils.isEmpty(videoUrl)) {
                        File file = new File(videoUrl);
                        boolean deleted = file.delete();

                        if (deleted) {
                            Log.i(Constants.TAG, "video file deleted successful");
                        }
                    }

                    mHelper.getVideoListDao().delete(video);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> deleteList() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    Dao<Video, Integer> daoVideoList = mHelper.getVideoListDao();
                    List<Video> noteList = daoVideoList.queryForAll();
                    daoVideoList.delete(noteList);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
