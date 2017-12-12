package com.tungnui.abccomputer.network.parser;
import com.tungnui.abccomputer.model.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/13/2016.
 */
public class CategoryParser {

    public ArrayList<Category> getCategory(String response) {
        if (response != null) {
            try {

                ArrayList<Category> categoryList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = 0;
                    String name = null, image = null, description = null;

                    if (jsonObject.has(ParserKey.KEY_ID)) {
                        id = jsonObject.getInt(ParserKey.KEY_ID);
                    }
                    if (jsonObject.has(ParserKey.KEY_NAME)) {
                        name = jsonObject.getString(ParserKey.KEY_NAME);
                    }
                    if (jsonObject.has(ParserKey.KEY_DESCRIPTION)) {
                        description = jsonObject.getString(ParserKey.KEY_DESCRIPTION);
                    }
                    if (jsonObject.has(ParserKey.KEY_IMAGE)) {
                        image = jsonObject.getJSONObject(ParserKey.KEY_IMAGE).getString(ParserKey.KEY_IMAGE_SOURCE);
                    }

                    categoryList.add(new Category(id, name, image, description, false));

                }
                return categoryList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
