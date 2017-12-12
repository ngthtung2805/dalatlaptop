package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 3/29/17.
 */

//TODO Need to add more category attributes for parsing more data

public class ReportTopSeller {
    public int productId;
    public String productName;
    public int quantity;

    public ReportTopSeller(int productId, String productName, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }
}
