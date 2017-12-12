package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 7/4/17.
 */

public class ShippingModel {
    public String firstName;
    public String lastName;
    public String companyName;
    public String address;
    public String city;
    public String state;
    public String postCode;
    public String country;

    public ShippingModel(String firstName, String lastName, String companyName,String address,
                         String city, String state, String postCode, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.companyName = companyName;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
        this.country = country;
    }
}
