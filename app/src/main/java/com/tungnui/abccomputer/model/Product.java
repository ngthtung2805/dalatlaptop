package com.tungnui.abccomputer.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by Nasir on 3/29/17.
 *
 * Here used model as a parcelable because of
 * passing product list activity to activity by intent
 * if necessary.
 */

//TODO Need to add more product attributes for parsing more data

public class Product implements Parcelable{
    public int id = 0;
    public int categoryId = 0;
    public String name;
    public String categoryName;
    public ArrayList<String> imageList;
    public String description;
    public double price;
    public String priceHtml;

    public Product(int id, int categoryId, String name, String categoryName, ArrayList<String> imageList,double price, String priceHtml, String description) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.categoryName = categoryName;
        this.imageList = imageList;
        this.price = price;
        this.priceHtml = priceHtml;
        this.description = description;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        categoryId = in.readInt();
        name = in.readString();
        imageList = in.createStringArrayList();
        price = in.readDouble();
        priceHtml = in.readString();
        description = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(categoryId);
        dest.writeString(name);
        dest.writeStringList(imageList);
        dest.writeDouble(price);
        dest.writeString(priceHtml);
        dest.writeString(description);
    }
}
