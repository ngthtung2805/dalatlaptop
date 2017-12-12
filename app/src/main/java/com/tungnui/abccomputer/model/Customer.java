package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 7/4/17.
 */

public class Customer {
    public String customerId;
    public String email;
    public String firstName;
    public String lastName;
    public String userName;

    public BillingModel billingModel;
    public ShippingModel shippingModel;

    public String avatarUrl;
    public String responseCode;

    public Customer(String customerId, String email, String firstName, String lastName, String userName, BillingModel billingModel,
                    ShippingModel shippingModel) {
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.billingModel = billingModel;
        this.shippingModel = shippingModel;
    }
    public Customer(String email, String firstName, String lastName, String userName, BillingModel billingModel) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.billingModel = billingModel;
    }
    public Customer(String customerId, String email, String firstName, String lastName, String userName, String avatarUrl, String responseCode) {
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.responseCode = responseCode;
    }
}
