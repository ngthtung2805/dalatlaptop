package com.tungnui.abccomputer.model;

/**
 * Created by Nasir on 7/4/17.
 */

public class BillingModel {
    public String firstName;
    public String lastName;
    public String companyName;
    public String address;
    public String city;
    public String state;
    public String postCode;
    public String country;
    public String email;
    public String phoneNumber;

    public BillingModel(String firstName, String lastName, String companyName, String address, String city, String state, String postCode,
                        String country, String email, String phoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
        this.country = country;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
