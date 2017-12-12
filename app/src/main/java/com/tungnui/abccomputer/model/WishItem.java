package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 7/11/17.
 */

public class WishItem {
    public int productId;
    public String name;
    public String images;
    public float price;
    public float ratting;
    public int orderCount;

    public WishItem(int productId, String name, String images, float price, float ratting, int orderCount){
        this.productId = productId;
        this.name = name;
        this.images = images;
        this.price = price;
        this.ratting = ratting;
        this.orderCount = orderCount;
    }
}
