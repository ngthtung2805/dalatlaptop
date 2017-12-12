package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.ProductDetailParser;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestSearchProducts extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestSearchProducts(Context context, int pageNumber, String searchKey, String categoryId, String minPrice, String maxPrice, String order, String orderBy) {
        super(context, HttpParams.API_PRODUCTS_LIST + pageNumber + HttpParams.KEY_SEARCH + searchKey + HttpParams.KEY_CATEGORY + categoryId + HttpParams.KEY_MIN_PRICE + minPrice
                + HttpParams.KEY_MAX_PRICE + maxPrice + HttpParams.KEY_ORDER + order + HttpParams.KEY_ORDER_BY + orderBy);
    }

    public RequestSearchProducts(Context context, int pageNumber, String searchKey, String minPrice, String maxPrice, String order, String orderBy) {
        super(context, HttpParams.API_PRODUCTS_LIST + pageNumber + HttpParams.KEY_SEARCH + searchKey + HttpParams.KEY_MIN_PRICE + minPrice
                + HttpParams.KEY_MAX_PRICE + maxPrice + HttpParams.KEY_ORDER + order + HttpParams.KEY_ORDER_BY + orderBy);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response", "Product: " + response);
            object = new ProductDetailParser().getProductList(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
