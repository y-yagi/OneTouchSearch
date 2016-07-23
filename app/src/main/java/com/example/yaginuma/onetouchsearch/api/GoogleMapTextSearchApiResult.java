package com.example.yaginuma.onetouchsearch.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaginuma on 16/05/11.
 */
public class GoogleMapTextSearchApiResult {
    JSONObject mResponse;
    JSONArray mResults;

    public GoogleMapTextSearchApiResult(String body) throws JSONException {
        mResponse = new JSONObject(body);
        this.mResults = this.mResponse.getJSONArray("results");
    }

    public String getName(int index) throws JSONException {
        return mResults.getJSONObject(index).getString("name");
    }

    public Double getLat(int index) throws JSONException {
        return mResults.getJSONObject(index).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
    }

    public Double getLng(int index) throws JSONException {
        return mResults.getJSONObject(index).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
    }

    public int resultCount() {
        return mResults.length();
    }
}
