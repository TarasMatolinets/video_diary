package interactor;

import database.IDataBase;
import database.VideoIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 12/4/2016.
 * Use case for get video note by title
 */

public class UseCaseGetVideoNotesByTitle extends UseCase {
    private final VideoIDataBase mDataBase;
    private String title;

    public UseCaseGetVideoNotesByTitle(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase, String title) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoIDataBase) IDataBase;
        this.title = title;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getVideoNotesByTitle(title);
    }
}
