package interactor;

import database.DataBase;
import database.NoteDataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseDeleteNoteId extends UseCase {
    private final int mId;
    private NoteDataBase mDataBase;

    public UseCaseDeleteNoteId(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, int id) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        mId = id;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.deleteItemById(mId);
    }
}
