package com.cst2335.finalproject;

import android.graphics.Bitmap;

/**
 * This is the Item class to create the soccer news item.
 * @author Feiqiong Deng
 * @version version 1
 */
public class Item {
    private String title;
    private String date;
    private String image;
    private String link;
    private String description;
    private Bitmap itemImage;
//    private long id;

    public Item(String title, String date, String image, Bitmap itemImage, String link, String description){
        this.title = title;
        this.date = date;
        this.image = image;
        this.link = link;
        this.description = description;
        this.itemImage = itemImage;
//        this.id = id;
    }

    public String getTitle() {return title;}
    public String getDate() {return date;}
    public String getImage() {return image;}
    public Bitmap getItemImage() {return itemImage;}
    public String getUrl() {return link;}
    public String getDescription() {return description;};
//    public long getId() {
//        return id;
//    }
}
