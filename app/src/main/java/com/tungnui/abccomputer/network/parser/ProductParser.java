package com.tungnui.abccomputer.network.parser;

import com.tungnui.abccomputer.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class ProductParser {

    // parse and return single product
    public Product getProduct(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                int id = 0, categoryId = 0;
                double price = 0;
                String name = null, categoryName = null, priceHtml = null, description = null;

                ArrayList<String> imageList = new ArrayList<>();

                if (jsonObject.has(ParserKey.KEY_ID)) {
                    id = jsonObject.getInt(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_CATEGORIES)) {
                    categoryId = getCategoryId(jsonObject.getJSONArray(ParserKey.KEY_CATEGORIES));
                    categoryName = getCategoryName(jsonObject.getJSONArray(ParserKey.KEY_CATEGORIES));
                }
                if (jsonObject.has(ParserKey.KEY_NAME)) {
                    name = jsonObject.getString(ParserKey.KEY_NAME);
                }
                if (jsonObject.has(ParserKey.KEY_IMAGES)) {
                    imageList.addAll(getImageList(jsonObject.getJSONArray(ParserKey.KEY_IMAGES)));
                }
                if (jsonObject.has(ParserKey.KEY_SELL_PRICE)) {
                    String strPrice = jsonObject.getString(ParserKey.KEY_SELL_PRICE);
                    if (strPrice != null && !strPrice.isEmpty())
                    price = jsonObject.getDouble(ParserKey.KEY_SELL_PRICE);
                }
                if (jsonObject.has(ParserKey.KEY_PRICE_HTML)) {
                    priceHtml = jsonObject.getString(ParserKey.KEY_PRICE_HTML);
                }
                if (jsonObject.has(ParserKey.KEY_DESCRIPTION)) {
                    description = jsonObject.getString(ParserKey.KEY_DESCRIPTION);
                }
                return new Product(id, categoryId, name, categoryName, imageList, price, priceHtml, description);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // return product list
    public ArrayList<Product> getProductList(String response) {
        if (response != null) {
            ArrayList<Product> dataList = new ArrayList<>();
            JSONObject jsonObject;

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        jsonObject = jsonArray.getJSONObject(i);
                        Product product = getProduct(jsonObject);

                        if (product != null) {
                            dataList.add(product);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataList;
        }
        return null;
    }


    // return image list
    private ArrayList<String> getImageList(JSONArray jsonArray) {
        ArrayList<String> productImages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject imageObject = jsonArray.getJSONObject(i);
                if (imageObject.has(ParserKey.KEY_IMAGE_SOURCE))
                    productImages.add(imageObject.getString(ParserKey.KEY_IMAGE_SOURCE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return productImages;
    }

    // return category id
    private int getCategoryId(JSONArray jsonArray) {
        // get first category id
        //TODO Need to get ids list
        int categoryId = 0;
        for (int i = 0; i < 1; i++) {  // 1 for first index
            try {
                JSONObject catObject = jsonArray.getJSONObject(i);
                if (catObject.has(ParserKey.KEY_ID))
                    categoryId = catObject.getInt(ParserKey.KEY_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categoryId;
    }
    // return category id
    private String getCategoryName(JSONArray jsonArray) {
        // get first category id
        //TODO Need to get name list
        String categoryName = null;
        for (int i = 0; i < 1; i++) {  // 1 for first index
            try {
                JSONObject catObject = jsonArray.getJSONObject(i);
                if (catObject.has(ParserKey.KEY_NAME))
                    categoryName = catObject.getString(ParserKey.KEY_NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categoryName;
    }
}
