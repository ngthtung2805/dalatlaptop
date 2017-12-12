package com.tungnui.abccomputer.network.helper;

import android.content.Context;
import android.util.Log;

import com.tungnui.abccomputer.model.LineItem;
import com.tungnui.abccomputer.model.OrderModel;
import com.tungnui.abccomputer.network.http.BaseHttp;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.network.params.HttpParams;
import com.tungnui.abccomputer.network.parser.OrderItemParser;
import com.tungnui.abccomputer.network.parser.ParserKey;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Nasir on 4/07/17.
 */

public class RequestOrder extends BaseHttp {

    private Object object;
    private ResponseListener responseListener;
    private OrderModel order;

    public RequestOrder(Context context, OrderModel order) {
        super(context, HttpParams.API_REQUEST_ORDER);
        this.order = order;
    }

    public void buildParams(){
        JSONObject jObjOrder = new JSONObject();
        try {
            jObjOrder.put(ParserKey.KEY_EMAIL,  order.email);
            jObjOrder.put(ParserKey.KEY_CUSTOMER_ID,  order.customerId);
            jObjOrder.put(ParserKey.KEY_PAYMENT_METHOD, order.paymentMethod);
            jObjOrder.put(ParserKey.KEY_PAYMENT_METHOD_TITLE, order.paymentMethodTitle);

            // Billing Info json
            JSONObject jObjBilling = new JSONObject();

            jObjBilling.put(ParserKey.KEY_FIRST_NAME,   order.billingModel.firstName);
            jObjBilling.put(ParserKey.KEY_LAST_NAME,    order.billingModel.lastName);
            jObjBilling.put(ParserKey.KEY_COMPANY,      order.billingModel.companyName);
            jObjBilling.put(ParserKey.KEY_ADDRESS,      order.billingModel.address);
            jObjBilling.put(ParserKey.KEY_CITY,         order.billingModel.city);
            jObjBilling.put(ParserKey.KEY_STATE_NAME,   order.billingModel.state);
            jObjBilling.put(ParserKey.KEY_POST_CODE,    order.billingModel.postCode);
            jObjBilling.put(ParserKey.KEY_COUNTRY_NAME, order.billingModel.country);
            jObjBilling.put(ParserKey.KEY_EMAIL,        order.billingModel.email);
            jObjBilling.put(ParserKey.KEY_PHONE_NUMBER, order.billingModel.phoneNumber);
            // set obj as a child into jObjCustomer
            jObjOrder.put(ParserKey.KEY_BILLING, jObjBilling);

            // Shipping Info json
            JSONObject jObjShipping = new JSONObject();

            jObjShipping.put(ParserKey.KEY_FIRST_NAME,  order.shippingModel.firstName);
            jObjShipping.put(ParserKey.KEY_LAST_NAME,   order.shippingModel.lastName);
            jObjShipping.put(ParserKey.KEY_COMPANY,     order.shippingModel.companyName);
            jObjShipping.put(ParserKey.KEY_ADDRESS,     order.shippingModel.address);
            jObjShipping.put(ParserKey.KEY_CITY,        order.shippingModel.city);
            jObjShipping.put(ParserKey.KEY_STATE_NAME,  order.shippingModel.state);
            jObjShipping.put(ParserKey.KEY_POST_CODE,   order.shippingModel.postCode);
            jObjShipping.put(ParserKey.KEY_COUNTRY_NAME,order.shippingModel.country);

            // set obj as a child into jObjCustomer
            jObjOrder.put(ParserKey.KEY_SHIPPING, jObjShipping);

            // Line items Info json
            JSONArray jsonArrayLine= new JSONArray();

            for (int i=0; i< order.lineItems.size(); i++){

                LineItem lineItem = order.lineItems.get(i);
                JSONObject objLine = new JSONObject();

                objLine.put(ParserKey.KEY_PRODUCT_ID, lineItem.productId);
                objLine.put(ParserKey.KEY_QUANTITY, lineItem.quantity);

                jsonArrayLine.put(objLine);
            }

            // set obj as a child into jObjCustomer
            jObjOrder.put(ParserKey.KEY_LINE_ITEMS, jsonArrayLine);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        postJsonParams(jObjOrder.toString());
    }
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    public void onBackgroundTask(String response) {
        if (response != null && !response.isEmpty()) {
            Log.e("Response : ", response);
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
