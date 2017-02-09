package com.mti.videodiary.data.storage.manager;

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

import database.VideoIDataBase;
import model.VideoDomain;
import rx.Observable;
import rx.Subscriber;

import static com.mti.videodiary.data.Constants.TAG;
import static com.mti.videodiary.data.storage.dao.Note.TITLE;
import static com.mti.videodiary.data.storage.dao.Video.ID;


/**
 * Created by Terry on 11/6/2016.
 * Implements {@link VideoIDataBase} for communicate with database
 */

public class VideoNoteIDataBaseFactory implements VideoIDataBase {

    private static final String EMPTY = "";
    private final DataBaseHelper mHelper;

    @Inject
    public VideoNoteIDataBaseFactory(DataBaseHelper helper) {
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
                    if (!accountList.isEmpty()) {
                        Video video = accountList.get(defaultValue);
                        DataToDomainTransformer transformer = new DataToDomainTransformer();
                        VideoDomain videoDomain = transformer.transform(video);

                        subscriber.onNext(videoDomain);
                    }
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<VideoDomain>> getVideoNotesByTitle(final String title) {
        return Observable.create(new Observable.OnSubscribe<List<VideoDomain>>() {
            @Override
            public void call(Subscriber<? super List<VideoDomain>> subscriber) {
                try {
                    QueryBuilder<Video, Integer> queryBuilder = mHelper.getVideoListDao().queryBuilder();
                    queryBuilder.where().like(TITLE, "%" + title + "%");

                    List<Video> listNotes = queryBuilder.query();

                    DataToDomainTransformer transformer = new DataToDomainTransformer();
                    List<VideoDomain> noteDomainList = transformer.transformVideoList(listNotes);

                    subscriber.onNext(noteDomainList);

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
                    deleteVideoLocally();

                    DomainToDataTransformer transformer = new DomainToDataTransformer();
                    Video videoDomain = transformer.transform(video);
                    mHelper.getVideoListDao().update(videoDomain);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }

            private void deleteVideoLocally() {
                if (video.isDeletedVideo()) {
                    File fileToDelete = new File(video.getVideoPath());
                    if (fileToDelete.exists()) {
                        boolean isDeleted = fileToDelete.delete();

                        if (isDeleted) {
                            //set empty paths for delete video
                            video.setVideoUrl(EMPTY);
                            video.setImageUrl(EMPTY);
                            Log.i(TAG, "file deleted successfully");
                        }
                    }
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

                    if (!TextUtils.isEmpty(imageUrl)) {
                        File file = new File(imageUrl);
                        boolean deleted = file.delete();

                        if (deleted) {
                            Log.i(Constants.TAG, "image file deleted successful");
                        }
                    }

                    if (!TextUtils.isEmpty(videoUrl)) {
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
