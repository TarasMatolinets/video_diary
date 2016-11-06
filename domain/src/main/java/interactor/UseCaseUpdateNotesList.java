package interactor;

import database.DataBase;
import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import model.NoteDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseUpdateNotesList extends UseCase {
    private final NoteDomain mNoteDomain;
    private NoteDataBase mDataBase;

    public UseCaseUpdateNotesList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, NoteDomain noteDomain, DataBase dataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        mNoteDomain = noteDomain;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.updateNoteList(mNoteDomain);
    }
}
