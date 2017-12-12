package com.tungnui.abccomputer.network.parser;
import com.tungnui.abccomputer.model.ProductReview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class ProductReviewParser {

    public ArrayList<ProductReview> getProductReviews(String response) {
        if (response != null) {
            try {

                ArrayList<ProductReview> reviewList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = 0;
                    float rating =0;
                    String createdDateTime = null, review = null, name = null, email = null;

                    if (jsonObject.has(ParserKey.KEY_ID)) {
                        id = jsonObject.getInt(ParserKey.KEY_ID);
                    }
                    if (jsonObject.has(ParserKey.KEY_CREATED_DATE)) {
                        createdDateTime = jsonObject.getString(ParserKey.KEY_CREATED_DATE);
                    }
                    if (jsonObject.has(ParserKey.KEY_RATING)) {
                        if (jsonObject.getString(ParserKey.KEY_RATING) != null) {
                            rating = Float.parseFloat(jsonObject.getString(ParserKey.KEY_RATING));
                        }
                    }
                    if (jsonObject.has(ParserKey.KEY_REVIEW)) {
                        review = jsonObject.getString(ParserKey.KEY_REVIEW);
                    }
                    if (jsonObject.has(ParserKey.KEY_NAME)) {
                        name = jsonObject.getString(ParserKey.KEY_NAME);
                    }
                    if (jsonObject.has(ParserKey.KEY_EMAIL)) {
                        email = jsonObject.getString(ParserKey.KEY_EMAIL);
                    }
                    reviewList.add(new ProductReview(id, createdDateTime, rating, review, name, email));
                }
                return reviewList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
