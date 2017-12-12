package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.ProductDetailParser;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestProducts extends BaseHttp {

    private Object object;
    private ResponseListener responseListener;
    private int type;

    // for all product of a specific category
    public RequestProducts(Context context, int pageNumber, int perPage, int categoryId, int type) {
        super(context, HttpParams.API_PRODUCTS_LIST + pageNumber + HttpParams.KEY_PER_PAGE + perPage + HttpParams.KEY_CATEGORY + categoryId);
        this.type = type;
    }

    // for featured and recent products
    public RequestProducts(Context context, int pageNumber, int perPage, String httpRequestType, int type) {
        super(context, HttpParams.API_PRODUCTS_LIST + pageNumber + HttpParams.KEY_PER_PAGE + perPage + httpRequestType);
        this.type = type;
    }

    // for popular products
    public RequestProducts(Context context, int type) {
        super(context, HttpParams.API_PRODUCTS_LIST + AppConstants.INITIAL_PAGE_NUMBER + HttpParams.KEY_PER_PAGE + AppConstants.MAX_POPULAR);
        this.type = type;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()) {
            Log.e("Response", "Product: " + response);
            if (type == AppConstants.TYPE_POPULAR) {
                object = new ProductDetailParser().getPopularList(response);
            } else {
                object = new ProductDetailParser().getProductList(response);
            }
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
