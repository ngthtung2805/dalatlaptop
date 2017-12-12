package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 3/29/17.
 */

//TODO Need to add more category attributes for parsing more data

public class Category {
    public int id;
    public String name;
    public String image;
    public String description;
    public boolean isSelected;

    public Category(int id, String name, String image, String description, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.isSelected = isSelected;
    }
}
