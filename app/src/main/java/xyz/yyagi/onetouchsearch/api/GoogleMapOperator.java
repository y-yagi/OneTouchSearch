package xyz.yyagi.onetouchsearch.api;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import xyz.yyagi.onetouchsearch.models.Position;
import xyz.yyagi.onetouchsearch.R;

/**
 * Created by yaginuma on 14/08/28.
 */
public class GoogleMapOperator {
    private GoogleMap mMap;
    private Marker mCurrentPosMarker;
    private static final int ZOOM = 15;

    public GoogleMapOperator(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void addMarkerToMap(String title, Double lat, Double lng, BitmapDescriptor icon) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title).icon(icon));
    }

    public void clear() {
        mMap.clear();
    }

    public void setCurrentPosMarkerToMap(Position pos) {
        if (mCurrentPosMarker != null) {
            mCurrentPosMarker.remove();
        }

        mCurrentPosMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(pos.lat, pos.lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current)));
    }

    public void moveCamera(Position pos) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
                new LatLng(pos.lat, pos.lng), ZOOM);
        mMap.moveCamera(cu);
    }

}
