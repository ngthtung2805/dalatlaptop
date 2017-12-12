package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.CountryParser;

/**
 * Created by Nasir on 4/07/17.
 */

public class RequestCountryPicker extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;

    public RequestCountryPicker(Context context) {
        super(context, HttpParams.API_GET_All_COUNTRIES);
    }
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response : ", response);
            object = new CountryParser().getCountries(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
