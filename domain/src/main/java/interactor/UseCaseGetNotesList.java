package interactor;

import database.IDataBase;
import database.NoteIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * Use case for get note list
 */

public class UseCaseGetNotesList extends UseCase {

    private final NoteIDataBase mDataBase;

    public UseCaseGetNotesList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteIDataBase) IDataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getListNotes();
    }
}
