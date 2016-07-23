package com.example.yaginuma.onetouchsearch.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yaginuma on 16/05/11.
 */
public class Position {
    public double lat;
    public double lng;
    private Context mContext;
    private SharedPreferences mPref;

    private final static String PREF_KEY = "position";
    private final static String PREF_LATITUDE_KEY = "latitude";
    private final static String PREF_LONGITUDE_KEY = "longitude";

    public Position(Context context) {
        this.mContext = context;
        this.mPref = mContext.getSharedPreferences(PREF_KEY, mContext.MODE_PRIVATE);
        // set default to Tokyo
        this.lat =  this.mPref.getFloat(PREF_LATITUDE_KEY, 35.681382f);
        this.lng =  this.mPref.getFloat(PREF_LONGITUDE_KEY, 139.766084f);
    }

    public String toString() {
        return lat + "," + lng;
    }

    public void apply() {
        SharedPreferences.Editor editor = this.mPref.edit();
        editor.putFloat(PREF_LATITUDE_KEY, (float)lat);
        editor.putFloat(PREF_LONGITUDE_KEY, (float)lng);
        editor.apply();
    }
}

