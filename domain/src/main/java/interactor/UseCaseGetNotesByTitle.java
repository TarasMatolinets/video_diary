package interactor;

import database.DataBase;
import database.NoteDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 12/4/2016.
 */

public class UseCaseGetNotesByTitle extends UseCase {
    private final NoteDataBase mDataBase;
    private String title;

    public UseCaseGetNotesByTitle(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, String title) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteDataBase) dataBase;
        this.title = title;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getNotesByTitle(title);
    }
}
