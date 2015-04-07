package xyz.yyagi.onetouchsearch.models;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.yyagi.onetouchsearch.Util;

/**
 * Created by yaginuma on 14/10/17.
 */
public class PlaceDataManager {
    private Context mContext;
    private Realm mRealm;
    private RealmResults<Place> mPlaceData = null;
    private long mLastVersion = 0;
    private long mNextVersion = 0;

    public PlaceDataManager(Context context) {
        this.mContext = context;
        mRealm = Realm.getInstance(context);
        getLastVersion();
        mNextVersion = Util.currentTime();
        if(hasPlaceData()) loadPlaceData();
    }

    private void loadPlaceData() {
        mPlaceData = mRealm.where(Place.class)
                .equalTo("version", mLastVersion).findAll();
    }

    private void getLastVersion() {
        mLastVersion = mRealm.where(Place.class).findAll().max("version").longValue();
    }

    public boolean hasPlaceData() {
        return (mLastVersion > 0);
    }

    public RealmResults<Place> get() {
        return mPlaceData;
    }

    public void save(String name, Double lat, Double lng, int type) {
        mRealm.beginTransaction();
        Place place = mRealm.createObject(Place.class);
        place.setName(name);
        place.setLatitude(lat);
        place.setLongitude(lng);
        place.setType(type);
        place.setVersion(mNextVersion);
        mRealm.commitTransaction();
    }
}
