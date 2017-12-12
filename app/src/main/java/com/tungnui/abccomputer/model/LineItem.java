package com.tungnui.abccomputer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nasir on 3/29/17.
 */

public class LineItem implements Parcelable{

    public String productName;
    public String productAttribute;
    public int productId;
    public int quantity;

    public LineItem(String productName, String productAttribute, int productId, int quantity) {
        this.productName = productName;
        this.productAttribute = productAttribute;
        this.productId = productId;
        this.quantity = quantity;
    }

    protected LineItem(Parcel in) {
        productName = in.readString();
        productAttribute = in.readString();
        productId = in.readInt();
        quantity = in.readInt();
    }

    public static final Creator<LineItem> CREATOR = new Creator<LineItem>() {
        @Override
        public LineItem createFromParcel(Parcel in) {
            return new LineItem(in);
        }

        @Override
        public LineItem[] newArray(int size) {
            return new LineItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productAttribute);
        dest.writeInt(productId);
        dest.writeInt(quantity);
    }
}
