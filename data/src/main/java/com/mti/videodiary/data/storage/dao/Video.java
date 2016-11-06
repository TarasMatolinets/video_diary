package com.mti.videodiary.data.storage.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Taras Matolinets on 27.02.15.
 */

@DatabaseTable
public class Video {
    public static final String TITLE = "title";
    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String VIDEO_URL = "videoUrl";
    public static final String IMAGE_URL = "imageUrl";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;
    @DatabaseField(columnName = VIDEO_URL)
    private String videoUrl;
    @DatabaseField(columnName = IMAGE_URL)
    private String imageUrl;
    @DatabaseField(columnName = TITLE)
    private String title;
    @DatabaseField(columnName = DESCRIPTION)
    private String description;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getId() {
        return id;
    }

    public String getVideoName() {
        return videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
