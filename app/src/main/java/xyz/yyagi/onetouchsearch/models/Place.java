package xyz.yyagi.onetouchsearch.models;

import android.support.annotation.ColorRes;

import java.io.Serializable;

/**
 * Created by yaginuma on 14/10/13.
 */
public class Place implements Serializable{
    private static final long serialVersionUID = 1L;
    public String name;
    public Double latitude;
    public Double longitude;
    public int type;

    public Place() { }

    public Place(String name, Double latitude, Double longitude, int type) {
        super();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }
}
