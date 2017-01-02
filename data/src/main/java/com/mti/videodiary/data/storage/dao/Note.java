package com.mti.videodiary.data.storage.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Taras Matolinets on 29.03.15.
 * Note model in data layer
 */
@DatabaseTable
public class Note {
    public static final String TITLE = "title";
    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    @DatabaseField(generatedId = true, columnName = ID)
    private int id;
    @DatabaseField(columnName = TITLE)
    private String title;
    @DatabaseField(columnName = DESCRIPTION)
    private String description;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
