package com.poc.evault.model;

/**
 * Created by DASP2 on 4/4/2017.
 */
public class Item {
    private long id;
    private String name;
    private String uploadDate;
    private String size;
    private String type;

    public Item() {
    }

    public Item(long id, String name, String uploadDate, String size, String type) {
        this.id = id;
        this.name = name;
        this.uploadDate = uploadDate;
        this.size = size;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
