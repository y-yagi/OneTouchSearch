package com.example.yaginuma.onetouchsearch.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.yaginuma.onetouchsearch.api.GoogleMapTextSearchApiResult;
import com.example.yaginuma.onetouchsearch.api.GooglePlaceAPIClient;
import com.example.yaginuma.onetouchsearch.model.GoogleMapOperator;
import com.example.yaginuma.onetouchsearch.model.Position;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleMapOperator mMapOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.yaginuma.onetouchsearch.R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.example.yaginuma.onetouchsearch.R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Position position = new Position(this);
        if(mMap != null) {
            mMap.setIndoorEnabled(false);
        }

        mMapOperator = new GoogleMapOperator(mMap);
        mMapOperator.setCurrentPosMarkerToMap(position);
        mMapOperator.moveCamera(position);

        GooglePlaceAPIClient googlePlaceApiClient = new GooglePlaceAPIClient(this);
        Call<ResponseBody> call = googlePlaceApiClient.textserach("神社", position.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        GoogleMapTextSearchApiResult apiResult = new GoogleMapTextSearchApiResult(response.body().string());
                        if (apiResult.resultCount() == 0) return;

                        for(int i = 0; i < apiResult.resultCount(); i++) {
                            mMapOperator.addMarkerToMap(
                                apiResult.getName(i),
                                apiResult.getLat(i),
                                apiResult.getLng(i),
                                mMapOperator.getIcon(0)
                            );

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "textsearch responce parse error");
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    Log.e(TAG, "textsearch responce is not success");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "textsearch get failure");
            }
        });
    }
}
