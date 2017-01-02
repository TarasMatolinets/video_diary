package interactor;

import database.IDataBase;
import database.VideoIDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseGetVideoNoteList extends UseCase {
    private VideoIDataBase mDataBase;

    public UseCaseGetVideoNoteList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, IDataBase IDataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoIDataBase) IDataBase;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.getListVideos();
    }
}
