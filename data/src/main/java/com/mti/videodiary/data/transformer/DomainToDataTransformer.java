package com.mti.videodiary.data.transformer;

import com.mti.videodiary.data.storage.dao.Note;
import com.mti.videodiary.data.storage.dao.Video;

import model.NoteDomain;
import model.VideoDomain;

/**
 * Created by Terry on 11/6/2016.
 */

public class DomainToDataTransformer {

    public Note transform(NoteDomain noteDomain) {

        Note note = new Note();
        note.setDescription(noteDomain.getDescription());
        note.setId(noteDomain.getId());
        note.setTitle(noteDomain.getTitle());

        return note;
    }

    public Video transform(VideoDomain videoDomain) {
        Video video = new Video();

        video.setTitle(videoDomain.getTitle());
        video.setId(videoDomain.getId());
        video.setDescription(videoDomain.getDescription());
        video.setImageUrl(videoDomain.getImageUrl());
        video.setVideoUrl(videoDomain.getVideoName());

        return video;
    }
}
