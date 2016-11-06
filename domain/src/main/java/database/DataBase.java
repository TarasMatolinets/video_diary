package database;

import java.util.List;

import model.NoteDomain;
import model.VideoDomain;
import rx.Observable;

/**
 * Interface for transfer object from presenter to DB in data layer
 */
public interface DataBase {

    Observable<List<NoteDomain>> getListNotes();

    Observable<List<VideoDomain>> getListVideoNotes();

    Observable<Void> deleteNoteById(int id);

    Observable<NoteDomain> getNoteByPosition(int id);

    Observable<Void> createNote(NoteDomain note);

    Observable<Void> updateNoteList(NoteDomain note);

    Observable<Void> deleteNotesList();
}
