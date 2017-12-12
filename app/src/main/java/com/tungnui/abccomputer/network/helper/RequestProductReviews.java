package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.ProductReviewParser;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestProductReviews extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestProductReviews(Context context, int productId) {   // tested with product id 75
        super(context, HttpParams.API_PRODUCT_DETAILS+productId+HttpParams.API_PRODUCT_REVIEWS);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()) {
            Log.e("Response", "reviews: " + response);
            object = new ProductReviewParser().getProductReviews(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
