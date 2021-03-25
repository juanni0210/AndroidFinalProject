package com.cst2335.finalproject;

public class Item {
    private String title;
    private String date;
    private String image;
    private String link;
    private String description;
//    private long id;

    public Item(String title, String date, String image, String link, String description){
        this.title = title;
        this.date = date;
        this.image = image;
        this.link = link;
        this.description = description;
//        this.id = id;
    }

    public String getTitle() {return title;}
    public String getDate() {return date;}
    public String getImage() {return image;}
    public String getUrl() {return link;}
    public String getDescription() {return description;};
//    public long getId() {
//        return id;
//    }
}
