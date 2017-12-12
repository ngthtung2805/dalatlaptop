package com.tungnui.abccomputer.network.parser;

import com.tungnui.abccomputer.model.Customer;

import org.json.JSONObject;

/**
 * Created by Nasir on 8/13/2016.
 */
public class CustomerParser {

    public Customer getCustomer(String response) {
        if (response != null) {
            try {

                JSONObject jsonObject = new JSONObject(response);

                String customerId = null, email = null, userName = null, firstName = null, lastName = null, avatarUrl = null, responseCode = null;

                if (jsonObject.has(ParserKey.KEY_ID)) {
                    customerId = jsonObject.getString(ParserKey.KEY_ID);
                }
                if (jsonObject.has(ParserKey.KEY_EMAIL)) {
                    email = jsonObject.getString(ParserKey.KEY_EMAIL);
                }

                if (jsonObject.has(ParserKey.KEY_USER_NAME)) {
                    userName = jsonObject.getString(ParserKey.KEY_USER_NAME);
                }

                if (jsonObject.has(ParserKey.KEY_FIRST_NAME)) {
                    firstName = jsonObject.getString(ParserKey.KEY_FIRST_NAME);
                }
                if (jsonObject.has(ParserKey.KEY_LAST_NAME)) {
                    lastName = jsonObject.getString(ParserKey.KEY_LAST_NAME);
                }
                if (jsonObject.has(ParserKey.KEY_AVATAR_URL)){
                    avatarUrl = jsonObject.getString(ParserKey.KEY_AVATAR_URL);
                }
                if (jsonObject.has(ParserKey.KEY_RESPONSE_CODE)) {
                    responseCode = jsonObject.getString(ParserKey.KEY_RESPONSE_CODE);
                }

                return new Customer(customerId, email, firstName, lastName, userName, avatarUrl, responseCode);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
