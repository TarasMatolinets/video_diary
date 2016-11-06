package com.mti.videodiary.data.storage.manager;

import com.j256.ormlite.dao.Dao;
import com.mti.videodiary.data.storage.DataBaseHelper;
import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.transformer.DataToDomainTransformer;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import database.DataBase;
import model.NoteDomain;
import model.VideoDomain;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Terry on 11/6/2016.
 * Implements {@link DataBase} for communicate with database
 */

public class DataBaseManagerFactory implements DataBase {

    private final DataBaseHelper mHelper;

    @Inject
    DataBaseManagerFactory(DataBaseHelper helper) {
        mHelper = helper;
    }

    @Override
    public Observable<List<NoteDomain>> getListNotes() {

        return Observable.create(new Observable.OnSubscribe<List<NoteDomain>>() {
            @Override
            public void call(Subscriber<? super List<NoteDomain>> subscriber) {
                try {
                    List<Note> noteList = mHelper.getNoteListDao().queryForAll();
                    Collections.reverse(noteList);

                    DataToDomainTransformer transformer = new DataToDomainTransformer();
                    List<NoteDomain> listDomain = transformer.transformNoteList(noteList);

                    subscriber.onNext(listDomain);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<VideoDomain>> getListVideoNotes() {
        return null;
    }

    @Override
    public Observable<Void> deleteNoteById(int id) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {

                try {
                    Dao<Note, Integer> daoVideoList = mHelper.getNoteListDao();
                    List<Note> noteList = daoVideoList.queryForAll();

                    Collections.reverse(noteList);
                    daoVideoList.delete(noteList);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<NoteDomain> getNoteByPosition(int id) {
        return Observable.create(new Observable.OnSubscribe<NoteDomain>() {
            @Override
            public void call(Subscriber<? super NoteDomain> subscriber) {
                try {
                    mHelper.getNoteListDao().deleteById(id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Void> createNote(NoteDomain note) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Note note = null;
                try {
                    List<Note> noteList = mHelper.getNoteListDao().queryForAll();
                    Collections.reverse(noteList);

                    note = noteList.get(id);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return note;
            }
        });
    }

    @Override
    public Observable<Void> updateNoteList(NoteDomain note) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    mHelper.getNoteListDao().create(note);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Void> deleteNotesList() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    mHelper.getNoteListDao().update(note);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
