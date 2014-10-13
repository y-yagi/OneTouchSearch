package xyz.yyagi.onetouchsearch.models;

import android.support.annotation.ColorRes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by yaginuma on 14/10/13.
 */
@Table(name = "Places")
public class Place extends Model{
    @Column(name = "Name")
    public String name;
    @Column(name = "Latitude")
    public Double latitude;
    @Column(name = "Longitude")
    public Double longitude;
    @Column(name = "Type")
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
