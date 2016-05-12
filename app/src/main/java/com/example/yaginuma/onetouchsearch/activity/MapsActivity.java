package com.example.yaginuma.onetouchsearch.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yaginuma.onetouchsearch.R;
import com.example.yaginuma.onetouchsearch.api.GoogleMapTextSearchApiResult;
import com.example.yaginuma.onetouchsearch.api.GooglePlaceAPIClient;
import com.example.yaginuma.onetouchsearch.model.GoogleMapOperator;
import com.example.yaginuma.onetouchsearch.model.Position;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleMapOperator mMapOperator;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    protected Location mLastLocation;

    private Position mCurrentPosition;
    private ArrayList<String> mSearchWordList;
    private int mRequestCount = 0;
    public static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchWordList = new ArrayList<>();
        SharedPreferences sharedPrefereces = PreferenceManager.getDefaultSharedPreferences(this);
        mSearchWordList.add(sharedPrefereces.getString(getString(R.string.pref_search_word1_key), ""));
        if (mSearchWordList.get(0).isEmpty()) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return;
        }
        mSearchWordList.add(sharedPrefereces.getString(getString(R.string.pref_search_word2_key), ""));

        setContentView(com.example.yaginuma.onetouchsearch.R.layout.activity_maps);

        buildGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.example.yaginuma.onetouchsearch.R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        mCurrentPosition.apply();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mCurrentPosition = new Position(this);
        if (mMap != null) {
            mMap.setIndoorEnabled(false);
        }

        mMapOperator = new GoogleMapOperator(mMap);
        mMapOperator.setCurrentPosMarkerToMap(mCurrentPosition);
        mMapOperator.moveCamera(mCurrentPosition);

        GooglePlaceAPIClient googlePlaceApiClient = new GooglePlaceAPIClient(this);

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        GoogleMapTextSearchApiResult apiResult = new GoogleMapTextSearchApiResult(response.body().string());
                        if (apiResult.resultCount() == 0) return;

                        for (int i = 0; i < apiResult.resultCount(); i++) {
                            mMapOperator.addMarkerToMap(
                                    apiResult.getName(i),
                                    apiResult.getLat(i),
                                    apiResult.getLng(i),
                                    mMapOperator.getIcon(mRequestCount)
                            );

                        }
                        mRequestCount++;
                    } catch (Exception e) {
                        Log.e(TAG, "textsearch responce parse error\n" + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "textsearch API is not success");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "textsearch API failured");
            }
        };

        Call<ResponseBody> searchCall1 = googlePlaceApiClient.textserach(mSearchWordList.get(0), mCurrentPosition.toString());
        searchCall1.enqueue(callback);

        if (!mSearchWordList.get(1).isEmpty()) {
            Call<ResponseBody> searchCall2 = googlePlaceApiClient.textserach(mSearchWordList.get(1), mCurrentPosition.toString());
            searchCall2.enqueue(callback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentPosition.lat = location.getLatitude();
        mCurrentPosition.lng = location.getLongitude();
        mMapOperator.setCurrentPosMarkerToMap(mCurrentPosition);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        startLocationUpdates();
    }

    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
