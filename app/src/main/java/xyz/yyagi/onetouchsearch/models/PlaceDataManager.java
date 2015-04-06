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

/**
 * Created by yaginuma on 14/10/17.
 */
public class PlaceDataManager {
    private Context mContext;
    private Realm mRealm;
    private RealmResults<Place> mPlaceData = null;
    private long mCurrentVersion = 0;

    public PlaceDataManager(Context context) {
        this.mContext = context;
        mRealm = Realm.getInstance(context);
        getCurrentVersion();
        if(hasPlaceData()) loadPlaceData();
    }

    private void loadPlaceData() {
        mPlaceData = mRealm.where(Place.class)
                .equalTo("version", mCurrentVersion).findAll();
    }

    private void getCurrentVersion() {
        mCurrentVersion = mRealm.where(Place.class).findAll().max("version").longValue();
    }

    public boolean hasPlaceData() {
        return (mCurrentVersion > 0);
    }

    public RealmResults<Place> get() {
        return mPlaceData;
    }

    public void save(String name, Double lat, Double lng, int type, long version) {
        mRealm.beginTransaction();
        Place place = mRealm.createObject(Place.class);
        place.setName(name);
        place.setLatitude(lat);
        place.setLongitude(lng);
        place.setType(type);
        place.setVersion(version);
        mRealm.commitTransaction();
    }
}
