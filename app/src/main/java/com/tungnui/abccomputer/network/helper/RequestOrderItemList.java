package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.OrderItemParser;

/**
 * Created by Nasir on 5/11/17.
 */

public class RequestOrderItemList extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestOrderItemList(Context context, String orderId) {
        super(context, HttpParams.API_ORDER_DETAILS +orderId);
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {

        if (response != null && !response.isEmpty()) {
            Log.e("Response", "orders item: " + response);
            object = new OrderItemParser().getOrderItem(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
