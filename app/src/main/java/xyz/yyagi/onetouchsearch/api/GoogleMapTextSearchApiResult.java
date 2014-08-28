package xyz.yyagi.onetouchsearch.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaginuma on 14/08/28.
 */
public class GoogleMapTextSearchApiResult {
    JSONObject mResponse;
    JSONArray mResults;

    public GoogleMapTextSearchApiResult(JSONObject response) throws JSONException{
        this.mResponse = response;
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
