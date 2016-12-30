package database;

import java.util.List;

import model.NoteDomain;
import model.VideoDomain;
import rx.Observable;

/**
 * Created by Terry on 11/6/2016.
 */

public interface VideoDataBase extends DataBase {
    Observable<List<VideoDomain>> getListVideos();

    Observable<VideoDomain> getVideoById(int id);

    Observable<List<VideoDomain>> getVideoNotesByTitle(String title);

    Observable<Void> createVideo(VideoDomain video);

    Observable<Void> updateVideoList(VideoDomain video);
}
