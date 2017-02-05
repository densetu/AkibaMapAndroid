package net.dentare.akibamapandroid.resources;

public class Category {
    private long id;
    private String name;
    private String english;
    private long parentId;

    public Category(long id, String name, String english, long parentId) {
        this.id = id;
        this.name = name;
        this.english = english;
        this.parentId = parentId;
    }

    public Category() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
