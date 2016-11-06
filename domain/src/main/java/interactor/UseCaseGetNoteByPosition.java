package interactor;

import database.DataBase;
import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseGetNoteByPosition extends UseCase {
    private final int mId;
    private final NoteDataBase mDataBase;

    public UseCaseGetNoteByPosition(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getNoteByPosition(mId);
    }
}
