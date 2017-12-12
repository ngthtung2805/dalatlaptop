package com.tungnui.abccomputer.model;

import java.util.ArrayList;

/**
 * Created by Nasir on 3/29/17.
 */


public class OrderItem {

    public String orderId;
    public float totalPrice ;
    public String status;
    public String orderDate;
    public String paidDate;
    public String completedDate;
    public ArrayList<LineItem> lineItems;

    public OrderItem(String orderId, float totalPrice, String status, String orderDate, String paidDate,
                     String completedDate, ArrayList<LineItem> lineItems) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
        this.paidDate = paidDate;
        this.completedDate = completedDate;
        this.lineItems = lineItems;
    }
}
