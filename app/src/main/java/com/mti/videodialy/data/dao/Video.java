package com.mti.videodialy.data.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Taras Matolinets on 27.02.15.
 */

@DatabaseTable
public class Video {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String videoUrl;
    @DatabaseField
    private String title;
    @DatabaseField
    private String description;

    public void setId(int id) {
        this.id = id;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getId() {
        return id;
    }

    public String getVideoUrl() {
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
