package interactor;

import database.IDataBase;
import database.NoteIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 12/4/2016.
 */

public class UseCaseGetNotesByTitle extends UseCase {
    private final NoteIDataBase mDataBase;
    private String title;

    public UseCaseGetNotesByTitle(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, String title) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (NoteIDataBase) IDataBase;
        this.title = title;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getNotesByTitle(title);
    }
}
