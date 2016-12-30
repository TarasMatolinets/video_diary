package interactor;

import database.DataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 12/4/2016.
 */

public class UseCaseGetVideoNotesByTitle extends UseCase {
    private final VideoDataBase mDataBase;
    private String title;

    public UseCaseGetVideoNotesByTitle(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase, String title) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoDataBase) dataBase;
        this.title = title;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getVideoNotesByTitle(title);
    }
}
