package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.ProductDetailParser;

import org.json.JSONObject;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestProductDetails extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestProductDetails(Context context, int ProductId) {
        super(context, HttpParams.API_PRODUCT_DETAILS+ProductId);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()){
            Log.e("Response", "Product details: " + response);
            try {
                object = new ProductDetailParser().getProductDetail(new JSONObject(response));
            }
            catch (Exception e){
                e.printStackTrace();
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
