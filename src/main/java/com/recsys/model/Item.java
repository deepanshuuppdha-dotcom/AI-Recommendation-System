package com.recsys.model;

public class Item {
    private final int id;
    private final String title;
    private final String category;
    private final String tags;
    private final String description;
    private final int popularity;

    public Item(int id, String title, String category, String tags, String description, int popularity) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.tags = tags;
        this.description = description;
        this.popularity = popularity;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getTags() { return tags; }
    public String getDescription() { return description; }
    public int getPopularity() { return popularity; }
}
