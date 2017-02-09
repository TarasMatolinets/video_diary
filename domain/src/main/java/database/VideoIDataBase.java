package database;

import java.util.List;

import model.VideoDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 * Interface for communicate with database
 */

public interface VideoIDataBase extends IDataBase {
    Observable<List<VideoDomain>> getListVideos();

    Observable<VideoDomain> getVideoById(int id);

    Observable<List<VideoDomain>> getVideoNotesByTitle(String title);

    Observable<Void> createVideo(VideoDomain video);

    Observable<Void> updateVideoList(VideoDomain video);

}
