package model;

/**
 * Created by Terry on 11/6/2016.
 * Note model in domain layer
 */

public class NoteDomain {
    private int id;
    private String title;
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
