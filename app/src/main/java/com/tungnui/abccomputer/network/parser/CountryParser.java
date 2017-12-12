package com.tungnui.abccomputer.network.parser;
import com.tungnui.abccomputer.model.Zone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class CountryParser {

    public ArrayList<Zone> getCountries(String response) {
        if (response != null) {
            try {

                ArrayList<Zone> countryList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = null, code = null;

                    if (jsonObject.has(ParserKey.KEY_NAME)) {
                        name = jsonObject.getString(ParserKey.KEY_NAME);
                    }
                    if (jsonObject.has(ParserKey.KEY_ID)) {
                        code = jsonObject.getString(ParserKey.KEY_ID);
                    }
                    countryList.add(new Zone(name, code));
                }
                return countryList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
