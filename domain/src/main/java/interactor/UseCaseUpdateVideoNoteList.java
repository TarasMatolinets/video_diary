package interactor;

import database.DataBase;
import database.VideoDataBase;
import executor.PostExecutionThread;
import executor.ThreadExecutor;
import model.VideoDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public class UseCaseUpdateVideoNoteList extends UseCase {

    private final VideoDataBase mDataBase;
    private final VideoDomain mVideoDomain;

    public UseCaseUpdateVideoNoteList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, VideoDomain videoDomain, DataBase dataBase) {
        super(threadExecutor, postExecutionThread);
        mDataBase = (VideoDataBase) dataBase;
        mVideoDomain = videoDomain;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return mDataBase.updateVideoList(mVideoDomain);
    }
}
