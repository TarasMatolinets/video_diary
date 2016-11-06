package com.mti.videodiary.data.transformer;

import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.storage.dao.Video;

import java.util.ArrayList;
import java.util.List;

import model.NoteDomain;
import model.VideoDomain;

/**
 * Created by Terry on 11/6/2016.
 * Transform objects from data to domain layer
 */

public class DataToDomainTransformer {

    public NoteDomain transform(Note note) {

        NoteDomain noteDomain = new NoteDomain();
        noteDomain.setDescription(note.getDescription());
        noteDomain.setId(note.getId());
        noteDomain.setTitle(note.getTitle());

        return noteDomain;
    }

    public VideoDomain transform(Video video) {
        VideoDomain videoDomain = new VideoDomain();

        videoDomain.setTitle(video.getTitle());
        videoDomain.setId(video.getId());
        videoDomain.setDescription(video.getDescription());
        videoDomain.setImageUrl(video.getImageUrl());
        videoDomain.setVideoUrl(video.getVideoName());

        return videoDomain;
    }

    public List<NoteDomain> transformNoteList(List<Note> listData) {
        List<NoteDomain> listDomain = new ArrayList<>();

        for (Note note : listData) {
            NoteDomain noteDomain = transform(note);
            listDomain.add(noteDomain);
        }

        return listDomain;
    }

    public List<VideoDomain> transformVideoList(List<Video> listData) {
        List<VideoDomain> listDomain = new ArrayList<>();

        for (Video video : listData) {
            VideoDomain noteDomain = transform(video);
            listDomain.add(noteDomain);
        }

        return listDomain;
    }
}
