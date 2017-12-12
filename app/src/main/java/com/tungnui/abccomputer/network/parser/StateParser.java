package com.tungnui.abccomputer.network.parser;

import com.tungnui.abccomputer.model.Zone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class StateParser {

    public ArrayList<Zone> getStates(String response) {
        if (response != null) {
            try {

                ArrayList<Zone> stateList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String code = null;

                    if (jsonObject.has(ParserKey.KEY_CODE)) {
                        code = jsonObject.getString(ParserKey.KEY_CODE);
                    }
                    stateList.add(new Zone(code, code));
                }
                return stateList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
