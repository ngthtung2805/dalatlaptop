package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 3/29/17.
 */


public class ProductReview {
    public int id;
    public String createdDateTime;
    public float rating;
    public String review;
    public String name;
    public String email;

    public ProductReview(int id, String createdDateTime, float rating, String review, String name, String email) {
        this.id = id;
        this.createdDateTime = createdDateTime;
        this.rating = rating;
        this.review = review;
        this.name = name;
        this.email = email;
    }
}
