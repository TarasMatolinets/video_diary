package com.mti.videodiary.data.storage.manager;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.mti.videodiary.data.storage.dao.Note;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by TarasMatolinets on 29.03.15.
 */
public class NoteDataManager extends DataBaseManager {
    private Context mContext;

    public NoteDataManager(Context ctx) {
        mContext = ctx;
    }

    public List<Note> getAllNotesList() {
        List<Note> noteList = null;
        try {
            noteList = mHelper.getNoteListDao().queryForAll();

            Collections.reverse(noteList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return noteList;
    }

    public void deleteNotesList() {
        List<Note> noteList;
        try {
            Dao<Note, Integer> daoVideoList = mHelper.getNoteListDao();
            noteList = daoVideoList.queryForAll();

            Collections.reverse(noteList);

            daoVideoList.delete(noteList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteNoteById(int id) {
        try {
            mHelper.getNoteListDao().deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Note getNoteByPosition(int id) {
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


    public void createNote(Note note) {
        try {
            mHelper.getNoteListDao().create(note);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNoteList(Note note) {
        try {
            mHelper.getNoteListDao().update(note);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
