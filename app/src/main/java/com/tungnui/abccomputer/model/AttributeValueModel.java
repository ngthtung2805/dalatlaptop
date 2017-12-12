package com.tungnui.abccomputer.model;

/**
 * Created by ashiq on 9/12/2017.
 */

public class AttributeValueModel {

    private String name;
    private boolean isSelect;

    public AttributeValueModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

}
