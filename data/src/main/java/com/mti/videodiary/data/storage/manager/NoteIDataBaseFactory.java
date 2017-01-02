package com.mti.videodiary.data.storage.manager;

import com.j256.ormlite.stmt.QueryBuilder;
import com.mti.videodiary.data.storage.DataBaseHelper;
import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.transformer.DataToDomainTransformer;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import database.NoteIDataBase;
import model.NoteDomain;
import rx.Observable;
import rx.Subscriber;

import static com.mti.videodiary.data.storage.dao.Note.ID;
import static com.mti.videodiary.data.storage.dao.Note.TITLE;

/**
 * Created by Terry on 11/6/2016.
 * Implements {@link NoteIDataBase} for communicate with database
 */

public class NoteIDataBaseFactory implements NoteIDataBase {

    private final DataBaseHelper mHelper;

    @Inject
    public NoteIDataBaseFactory(DataBaseHelper helper) {
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
    public Observable<Void> deleteItemById(final int id) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    QueryBuilder<Note, Integer> queryBuilder = mHelper.getNoteListDao().queryBuilder();
                    queryBuilder.where().eq(ID, id);

                    List<Note> accountList = queryBuilder.query();

                    int defaultValue = 0;
                    Note note = accountList.get(defaultValue);
                    mHelper.getNoteListDao().delete(note);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<NoteDomain> getNoteByPosition(final int id) {
        return Observable.create(new Observable.OnSubscribe<NoteDomain>() {
            @Override
            public void call(Subscriber<? super NoteDomain> subscriber) {
                try {
                    QueryBuilder<Note, Integer> queryBuilder = mHelper.getNoteListDao().queryBuilder();
                    queryBuilder.where().eq(ID, id);

                    List<Note> accountList = queryBuilder.query();

                    int defaultValue = 0;
                    Note note = accountList.get(defaultValue);

                    DataToDomainTransformer transformer = new DataToDomainTransformer();
                    NoteDomain noteDomain = transformer.transform(note);
                    subscriber.onNext(noteDomain);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> createNote(final String description, final String title) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    Note note = new Note();
                    note.setTitle(title);
                    note.setDescription(description);

                    mHelper.getNoteListDao().create(note);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> updateNoteList(final String description, final String title, final int position) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    QueryBuilder<Note, Integer> queryBuilder = mHelper.getNoteListDao().queryBuilder();
                    queryBuilder.where().eq(ID, position);

                    List<Note> accountList = queryBuilder.query();

                    int defaultValue = 0;

                    Note note = accountList.get(defaultValue);
                    note.setDescription(description);
                    note.setTitle(title);

                    mHelper.getNoteListDao().update(note);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    @Override
    public Observable<List<NoteDomain>> getNotesByTitle(final String title) {
        return Observable.create(new Observable.OnSubscribe<List<NoteDomain>>() {
            @Override
            public void call(Subscriber<? super List<NoteDomain>> subscriber) {
                try {
                    QueryBuilder<Note, Integer> queryBuilder = mHelper.getNoteListDao().queryBuilder();
                    queryBuilder.where().like(TITLE, "%" + title + "%");

                    List<Note> listNotes = queryBuilder.query();

                    DataToDomainTransformer transformer = new DataToDomainTransformer();
                    List<NoteDomain> noteDomainList = transformer.transformNoteList(listNotes);

                    subscriber.onNext(noteDomainList);

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
                    List<Note> noteList = mHelper.getNoteListDao().queryForAll();
                    mHelper.getNoteListDao().delete(noteList);

                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
