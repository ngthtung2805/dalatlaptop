package com.tungnui.abccomputer.model;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/23/17.
 */

public class ProductAttribute {
    public int id;
    public String name;
    public ArrayList<AttributeValueModel> optionList;

    public ProductAttribute(int id, String name, ArrayList<AttributeValueModel> options){
        this.id = id;
        this.name = name;
        this.optionList = options;
    }
}
