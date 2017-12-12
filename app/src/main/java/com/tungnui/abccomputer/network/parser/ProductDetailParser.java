package com.tungnui.abccomputer.network.parser;

import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.model.AttributeValueModel;
import com.tungnui.abccomputer.model.Category;
import com.tungnui.abccomputer.model.ProductAttribute;
import com.tungnui.abccomputer.model.ProductDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class ProductDetailParser {

    // parse and return product det
    public ProductDetail getProductDetail(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                boolean publishStatus = false, onSaleStatus = false, inStockStatus = false, isReviewAllow = false;
                int id = 0, categoryId = 0, totalSale = 0, totalRating = 0;
                float averageRating = 0, regularPrice = 0, sellPrice = 0;
                String name = null, shortDescription = null, linkAddress = null, categoryName = null, priceHtml = null, type = null, createdDateTime = null,
                        modifiedDateTime = null, description = null;

                ArrayList<String> imageList = new ArrayList<>();
                ArrayList<Integer> relatedProdIds = new ArrayList<>();
                ArrayList<Category> relatedCategoryList = new ArrayList<>();
                ArrayList<ProductAttribute> attributeList = new ArrayList<>();

                if (jsonObject.has(ParserKey.KEY_ID)) {
                    id = jsonObject.getInt(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_NAME)) {
                    name = jsonObject.getString(ParserKey.KEY_NAME);
                }
                if (jsonObject.has(ParserKey.KEY_SHORT_DESC)) {
                    shortDescription = jsonObject.getString(ParserKey.KEY_SHORT_DESC);
                }
                if (jsonObject.has(ParserKey.KEY_IMAGES)) {
                    imageList.addAll(getImageList(jsonObject.getJSONArray(ParserKey.KEY_IMAGES)));
                }
                if (jsonObject.has(ParserKey.KEY_LINK_ADDRESS)) {
                    linkAddress = jsonObject.getString(ParserKey.KEY_LINK_ADDRESS);
                }
                if (jsonObject.has(ParserKey.KEY_CATEGORIES)) {
                    categoryId = getCategoryId(jsonObject.getJSONArray(ParserKey.KEY_CATEGORIES));
                    categoryName = getCategoryName(jsonObject.getJSONArray(ParserKey.KEY_CATEGORIES));
                }
                if (jsonObject.has(ParserKey.KEY_RELATED_IDS)) {
                    relatedProdIds.addAll(getProductIds(jsonObject.getJSONArray(ParserKey.KEY_RELATED_IDS)));
                }
                if (jsonObject.has(ParserKey.KEY_CATEGORIES)) {
                    relatedCategoryList.addAll(getCategories(jsonObject.getJSONArray(ParserKey.KEY_CATEGORIES)));
                }
                if (jsonObject.has(ParserKey.KEY_REGULAR_PRICE)) {
                    if (!jsonObject.getString(ParserKey.KEY_REGULAR_PRICE).isEmpty()) {
                        regularPrice = Float.parseFloat(jsonObject.getString(ParserKey.KEY_REGULAR_PRICE));
                    }
                }
                if (jsonObject.has(ParserKey.KEY_SELL_PRICE)) {
                    if (!jsonObject.getString(ParserKey.KEY_SELL_PRICE).isEmpty()) {
                        sellPrice = Float.parseFloat(jsonObject.getString(ParserKey.KEY_SELL_PRICE));
                    }
                }
                if (jsonObject.has(ParserKey.KEY_TOTAL_SALE)) {
                    totalSale = jsonObject.getInt(ParserKey.KEY_TOTAL_SALE);
                }
                if (jsonObject.has(ParserKey.KEY_PRICE_HTML)) {
                    priceHtml = jsonObject.getString(ParserKey.KEY_PRICE_HTML);
                }
                if (jsonObject.has(ParserKey.KEY_TYPE)) {
                    type = jsonObject.getString(ParserKey.KEY_TYPE);
                }
                if (jsonObject.has(ParserKey.KEY_CREATED_DATE)) {
                    createdDateTime = jsonObject.getString(ParserKey.KEY_CREATED_DATE);
                }
                if (jsonObject.has(ParserKey.KEY_MODIFIED_DATE)) {
                    modifiedDateTime = jsonObject.getString(ParserKey.KEY_MODIFIED_DATE);
                }
                if (jsonObject.has(ParserKey.KEY_DESCRIPTION)) {
                    description = jsonObject.getString(ParserKey.KEY_DESCRIPTION);
                }
                if (jsonObject.has(ParserKey.KEY_TOTAL_RATING)) {
                    totalRating = jsonObject.getInt(ParserKey.KEY_TOTAL_RATING);
                }
                if (jsonObject.has(ParserKey.KEY_AVERAGE_RATING)) {
                    if (!jsonObject.getString(ParserKey.KEY_AVERAGE_RATING).isEmpty()) {
                        averageRating = Float.parseFloat(jsonObject.getString(ParserKey.KEY_AVERAGE_RATING));
                    }
                }
                if (jsonObject.has(ParserKey.KEY_PUBLISH_STATUS)) {
                    publishStatus = jsonObject.getBoolean(ParserKey.KEY_PUBLISH_STATUS);
                }
                if (jsonObject.has(ParserKey.KEY_ON_SALE)) {
                    onSaleStatus = jsonObject.getBoolean(ParserKey.KEY_ON_SALE);
                }
                if (jsonObject.has(ParserKey.KEY_IN_STOCK)) {
                    inStockStatus = jsonObject.getBoolean(ParserKey.KEY_IN_STOCK);
                }
                if (jsonObject.has(ParserKey.KEY_REVIEW_ALLOW)) {
                    isReviewAllow = jsonObject.getBoolean(ParserKey.KEY_REVIEW_ALLOW);
                }
                if (jsonObject.has(ParserKey.KEY_ATTRIBUTE)) {
                    attributeList.addAll(getAttributes(jsonObject.getJSONArray(ParserKey.KEY_ATTRIBUTE)));
                }

                return new ProductDetail(id, name, shortDescription, categoryId, categoryName, linkAddress, imageList, relatedProdIds, relatedCategoryList,
                        regularPrice, sellPrice, totalSale, priceHtml, type, createdDateTime, modifiedDateTime, description, totalRating,
                        averageRating, publishStatus, onSaleStatus, inStockStatus, isReviewAllow, attributeList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // return product list
    public ArrayList<ProductDetail> getProductList(String response) {
        if (response != null) {
            ArrayList<ProductDetail> dataList = new ArrayList<>();
            JSONObject jsonObject;

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        jsonObject = jsonArray.getJSONObject(i);
                        ProductDetail product = getProductDetail(jsonObject);

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

    // return product list
    public ArrayList<ProductDetail> getPopularList(String response) {
        if (response != null) {
            ArrayList<ProductDetail> popularList = new ArrayList<>();
            JSONObject jsonObject;

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        jsonObject = jsonArray.getJSONObject(i);
                        ProductDetail product = getProductDetail(jsonObject);

                        if (product != null) {
                            if (product.averageRating > AppConstants.VALUE_ZERO) {
                                popularList.add(product);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return popularList;
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

    // parse related product ids
    private ArrayList<Integer> getProductIds(JSONArray jsonArray) {

        ArrayList<Integer> productIds = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                int productId = jsonArray.getInt(i);
                productIds.add(productId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productIds;
    }
    // parse category list
    private ArrayList<Category> getCategories(JSONArray jsonArray) {

        ArrayList<Category> categoryList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int categoryId = 0; String categoryName = null;

                if (jsonObject.has(ParserKey.KEY_ID)) {
                    categoryId = jsonObject.getInt(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_NAME)) {
                    categoryName = jsonObject.getString(ParserKey.KEY_NAME);
                }
                categoryList.add(new Category(categoryId, categoryName, null, null, false));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return categoryList;
    }
    // parse category list
    private ArrayList<ProductAttribute> getAttributes(JSONArray jsonArray) {

        ArrayList<ProductAttribute> attributeList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = 0;
                String name = null;
                ArrayList<AttributeValueModel> optionList = new ArrayList<>();


                if (jsonObject.has(ParserKey.KEY_ID)) {
                    id = jsonObject.getInt(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_NAME)) {
                    name = jsonObject.getString(ParserKey.KEY_NAME);
                }

                if (jsonObject.has(ParserKey.KEY_OPTIONS)) {
                    JSONArray jsonOptions = jsonObject.getJSONArray(ParserKey.KEY_OPTIONS);

                    for (int j = 0; j < jsonOptions.length(); j++) {
                        optionList.add(new AttributeValueModel(jsonOptions.get(j).toString()));
                    }
                }
                attributeList.add(new ProductAttribute(id, name, optionList));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return attributeList;
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
