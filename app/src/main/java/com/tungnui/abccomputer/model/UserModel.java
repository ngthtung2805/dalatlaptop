package com.tungnui.abccomputer.model;

import java.io.Serializable;

/**
 * Created by Ashiq on 5/31/16.
 */
public class UserModel implements Serializable {

    public int userID;
    public String emailId;
    public String password;
    public String firstName;
    public String lastName;
    public String userName;
    public String photoPath;


    public String companyName;
    public String mobileNumber;


    public String streetAddress;
    public String city;
    public String district;
    public String postalCode;
    public String country;
    public String countryName;
    public String districtName;

    public String registrationType;
    public String authorizationCode;

    public UserModel() {}

    public UserModel(String emailId, String password, String firstName, String lastName, String userName, String mobileNumber,
                     String streetAddress, String city, String district, String postalCode, String country, String countryName, String districtName) {
        this.emailId = emailId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.mobileNumber = mobileNumber;

        this.streetAddress =  streetAddress;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
        this.country = country;
        this.countryName = countryName;
        this.districtName = districtName;
    }
    public UserModel(String firstName, String lastName, String emailId, String userName, String photoPath) {
        this.emailId = emailId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.photoPath = photoPath;
    }

    public UserModel(String registrationType, String authorizationCode) {
        this.registrationType = registrationType;
        this.authorizationCode = authorizationCode;
    }

    public UserModel(String email, String phone, String registrationType) {
        this.emailId = email;
        this.mobileNumber = phone;
        this.registrationType = registrationType;
    }

    public UserModel(int userID, String firstName, String lastName, String emailId, String mobileNumber,
                     String streetAddress, String city, String district, String postalCode, String country, String countryName, String districtName) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        this.streetAddress =  streetAddress;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
        this.country = country;
        this.countryName = countryName;
        this.districtName = districtName;
    }
}
