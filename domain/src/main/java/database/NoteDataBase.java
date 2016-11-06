package database;

import java.util.List;

import model.NoteDomain;
import rx.Observable;

/**
 * Interface for transfer object from presenter to DB in data layer
 */
public interface NoteDataBase extends DataBase{

    Observable<List<NoteDomain>> getListNotes();

    Observable<NoteDomain> getNoteByPosition(int id);

    Observable<Void> createNote(NoteDomain note);

    Observable<Void> updateNoteList(NoteDomain note);
}
