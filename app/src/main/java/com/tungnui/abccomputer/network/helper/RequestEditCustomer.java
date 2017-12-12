package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.model.Customer;
import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.CustomerParser;
import com.tungnui.abccomputer.network.parser.ParserKey;

import org.json.JSONObject;

/**
 * Created by Nasir on 4/07/17.
 */

public class RequestEditCustomer extends BaseHttp {
    private Object object;
    private ResponseListener responseListener;
    private Customer customer;

    public RequestEditCustomer(Context context, Customer customer) {
        super(context, HttpParams.API_EDIT_CUSTOMER + customer.customerId);
        this.customer = customer;
    }

    public void buildParams(){
        JSONObject jObjCustomer = new JSONObject();
        try {
            jObjCustomer.put(ParserKey.KEY_EMAIL,       customer.email);
            jObjCustomer.put(ParserKey.KEY_FIRST_NAME,  customer.firstName);
            jObjCustomer.put(ParserKey.KEY_LAST_NAME,   customer.lastName);
            jObjCustomer.put(ParserKey.KEY_USER_NAME,   customer.userName);

            // Billing Info json
            JSONObject jObjBilling = new JSONObject();
            jObjBilling.put(ParserKey.KEY_FIRST_NAME,   customer.billingModel.firstName);
            jObjBilling.put(ParserKey.KEY_LAST_NAME,    customer.billingModel.lastName);
            jObjBilling.put(ParserKey.KEY_COMPANY,      customer.billingModel.companyName);
            jObjBilling.put(ParserKey.KEY_ADDRESS,      customer.billingModel.address);
            jObjBilling.put(ParserKey.KEY_CITY,         customer.billingModel.city);
            jObjBilling.put(ParserKey.KEY_STATE_NAME,   customer.billingModel.state);
            jObjBilling.put(ParserKey.KEY_POST_CODE,    customer.billingModel.postCode);
            jObjBilling.put(ParserKey.KEY_COUNTRY_NAME, customer.billingModel.country);
            jObjBilling.put(ParserKey.KEY_EMAIL,        customer.billingModel.email);
            jObjBilling.put(ParserKey.KEY_PHONE_NUMBER, customer.billingModel.phoneNumber);
            // set obj as a child into jObjCustomer
            jObjCustomer.put(ParserKey.KEY_BILLING, jObjBilling);

            // Shipping Info json
            JSONObject jObjShipping = new JSONObject();
            jObjShipping.put(ParserKey.KEY_FIRST_NAME,  customer.shippingModel.firstName);
            jObjShipping.put(ParserKey.KEY_LAST_NAME,   customer.shippingModel.lastName);
            jObjShipping.put(ParserKey.KEY_COMPANY,     customer.shippingModel.companyName);
            jObjShipping.put(ParserKey.KEY_ADDRESS,     customer.shippingModel.address);
            jObjShipping.put(ParserKey.KEY_CITY,        customer.shippingModel.city);
            jObjShipping.put(ParserKey.KEY_STATE_NAME,  customer.shippingModel.state);
            jObjShipping.put(ParserKey.KEY_POST_CODE,   customer.shippingModel.postCode);
            jObjShipping.put(ParserKey.KEY_COUNTRY_NAME,customer.shippingModel.country);
            // set obj as a child into jObjCustomer
            jObjCustomer.put(ParserKey.KEY_SHIPPING, jObjShipping);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        putJsonParams(jObjCustomer.toString());
    }
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response : ", response);
            object = new CustomerParser().getCustomer(response);
        }
    }

    @Override
    public void onTaskComplete() {
        if (responseListener != null) {
            responseListener.onResponse(object);
        }
    }
}
