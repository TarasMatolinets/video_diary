package interactor;

import database.IDataBase;
import database.NoteIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * Use case for get note by id
 */

public class UseCaseGetNoteById extends UseCase {
    private final int mId;
    private final NoteIDataBase mDataBase;

    public UseCaseGetNoteById(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteIDataBase) IDataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getNoteByPosition(mId);
    }
}
