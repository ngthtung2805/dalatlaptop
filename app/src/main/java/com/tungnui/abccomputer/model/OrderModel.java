package com.tungnui.abccomputer.model;

import java.util.ArrayList;

/**
 * Created by Nasir on 3/29/17.
 */

public class OrderModel {

    public String customerId;
    public String email;
    public String paymentMethod;
    public String paymentMethodTitle;
    public String transactionId;

    public BillingModel billingModel;
    public ShippingModel shippingModel;
    public ArrayList<LineItem> lineItems;

    public OrderModel(String customerId, String email, String paymentMethod,String paymentMethodTitle, BillingModel billing, ShippingModel shipping,
                       ArrayList<LineItem> lineItems, String transactionId) {
        this.customerId = customerId;
        this.email = email;
        this.paymentMethod = paymentMethod;
        this.paymentMethodTitle = paymentMethodTitle;
        this.billingModel = billing;
        this.shippingModel = shipping;
        this.lineItems = lineItems;
        this.transactionId = transactionId;
    }
}
