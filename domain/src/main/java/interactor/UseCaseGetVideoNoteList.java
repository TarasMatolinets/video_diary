package interactor;

import database.DataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseGetVideoNoteList extends UseCase {
    private VideoDataBase mDataBase;

    public UseCaseGetVideoNoteList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, DataBase dataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoDataBase) dataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getListVideos();
    }
}
