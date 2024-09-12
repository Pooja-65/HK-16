package com.example.epinect.Activities.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String, String> parseJsonObject(JSONObject object) {
        HashMap<String, String> dataList = new HashMap<>();
        try {
            String name = object.getString("name");
            double latitude = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double longitude = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            dataList.put("name", name);
            dataList.put("lat", String.valueOf(latitude));
            dataList.put("lng", String.valueOf(longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                HashMap<String, String> data = parseJsonObject(jsonArray.getJSONObject(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object) {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}

