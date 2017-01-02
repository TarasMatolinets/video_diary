package database;

import java.util.List;

import model.NoteDomain;
import rx.Observable;

/**
 * Interface for communicate with note database
 */
public interface NoteIDataBase extends IDataBase {

    Observable<List<NoteDomain>> getListNotes();

    Observable<List<NoteDomain>> getNotesByTitle(String title);

    Observable<NoteDomain> getNoteByPosition(int id);

    Observable<Void> createNote(String description, String title);

    Observable<Void> updateNoteList(String description, String title, int position);
}
