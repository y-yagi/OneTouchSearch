package xyz.yyagi.onetouchsearch.models;

import android.support.annotation.ColorRes;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by yaginuma on 14/10/13.
 */
public class Place extends RealmObject {
    private String name;
    private double latitude;
    private double longitude;
    private long   version;
    private int type; // used by the judgment of the icon color


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
