package com.tungnui.abccomputer.network.parser;

import com.tungnui.abccomputer.model.OrderItem;
import com.tungnui.abccomputer.model.LineItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class OrderItemParser {

    public OrderItem getOrderItem(String response) {
        if (response != null) {
            try {

                JSONObject jsonObject = new JSONObject(response);

                float totalPrice = 0;
                String orderId = null, status = null, orderDate = null, paidDate = null, completedDate = null;
                ArrayList<LineItem> lineItems = new ArrayList<>();

                if (jsonObject.has(ParserKey.KEY_ID)) {
                    orderId = jsonObject.getString(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_TOTOL_PRICE)) {
                    totalPrice = Float.parseFloat(jsonObject.getString(ParserKey.KEY_TOTOL_PRICE));
                }
                if (jsonObject.has(ParserKey.KEY_STATUS)) {
                    status = jsonObject.getString(ParserKey.KEY_STATUS);
                }
                if (jsonObject.has(ParserKey.KEY_CREATED_DATE)) {
                    orderDate = jsonObject.getString(ParserKey.KEY_CREATED_DATE);
                }
                if (jsonObject.has(ParserKey.KEY_PAID_DATE)) {
                    paidDate = jsonObject.getString(ParserKey.KEY_PAID_DATE);
                }
                if (jsonObject.has(ParserKey.KEY_COMPLETED_DATE)) {
                    completedDate = jsonObject.getString(ParserKey.KEY_COMPLETED_DATE);
                }
                if (jsonObject.has(ParserKey.KEY_LINE_ITEMS)) {
                    lineItems.addAll(getOrderItemList(jsonObject.getJSONArray(ParserKey.KEY_LINE_ITEMS)));
                }
                return new OrderItem(orderId, totalPrice, status, orderDate, paidDate, completedDate, lineItems);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // return order list
    public ArrayList<OrderItem> getOrderList(String response) {
        if (response != null) {
            ArrayList<OrderItem> orderList = new ArrayList<>();
            //JSONObject jsonObject;

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        //jsonObject = jsonArray.getJSONObject(i);
                        OrderItem orderItem = getOrderItem(jsonArray.get(i).toString());

                        if (orderItem != null) {
                            orderList.add(orderItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return orderList;
        }
        return null;
    }

    // return order items
    public ArrayList<LineItem> getOrderItemList(JSONArray jsonArray) {
        if (jsonArray != null) {
            ArrayList<LineItem> dataList = new ArrayList<>();
            JSONObject jsonObject;

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        jsonObject = jsonArray.getJSONObject(i);

                        int id = 0, quantity = 0;
                        float price = 0;
                        String name = null;

                        if (jsonObject.has(ParserKey.KEY_PRODUCT_ID)) {
                            id = jsonObject.getInt(ParserKey.KEY_PRODUCT_ID);
                        }
                        if (jsonObject.has(ParserKey.KEY_NAME)) {
                            name = jsonObject.getString(ParserKey.KEY_NAME);
                        }
                        if (jsonObject.has(ParserKey.KEY_PRICE)) {
                            price = Float.parseFloat(jsonObject.getString(ParserKey.KEY_PRICE));
                        }
                        if (jsonObject.has(ParserKey.KEY_QUANTITY)) {
                            quantity = jsonObject.getInt(ParserKey.KEY_QUANTITY);
                        }

                        if (name != null) {
                            // TODO: Check its dependency
                            dataList.add(new LineItem("", "", id, quantity));
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
}
