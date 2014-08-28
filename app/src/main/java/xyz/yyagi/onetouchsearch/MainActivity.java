package xyz.yyagi.onetouchsearch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.location.LocationListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.yyagi.onetouchsearch.api.GoogleMapApiClient;
import xyz.yyagi.onetouchsearch.api.GoogleMapOperator;
import xyz.yyagi.onetouchsearch.api.GoogleMapTextSearchApiResult;

public class MainActivity extends FragmentActivity
        implements LocationListener , Response.Listener<JSONObject>, Response.ErrorListener {

    public LocationManager mLocationManager;
    private boolean mDisplayedMarker = false;;
    private String mGooglePlaceAPIKey;
    private Position mCurrentPosition;
    private View mProgressView;
    private View mMapFragment;
    private GoogleMapOperator mMapOperator;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGooglePlaceAPIKey = getString(R.string.google_place_api_key);
        mCurrentPosition = new Position(this);
        mProgressView = findViewById(R.id.progress);
        mMapFragment = findViewById(R.id.map) ;

        showProgress(true);
        setLocationProvider();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        mLocationManager.removeUpdates(this);
        mCurrentPosition.apply();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        if (mMapOperator == null) {
            GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMapOperator = new GoogleMapOperator(map);
            mMapOperator.moveCamera(mCurrentPosition);
            mMapOperator.setCurrentPosMarkerToMap(mCurrentPosition);
        }
    }

    private void setLocationProvider() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Boolean mLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!mLocationEnabled) {
            Toast.makeText(this, getString(R.string.error_location_setting), Toast.LENGTH_LONG).show();
            Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingIntent);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);  // 10秒/10m間隔
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentPosition.setLat(location.getLatitude());
        mCurrentPosition.setLng(location.getLongitude());
        showProgress(false);

        if (!mDisplayedMarker) {
            fetchPlaces();
            mMapOperator.moveCamera(mCurrentPosition);
            mDisplayedMarker = true;
        }
        mMapOperator.setCurrentPosMarkerToMap(mCurrentPosition);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void fetchPlaces() {
        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(this);
        GoogleMapApiClient googleMapiApiClient = new GoogleMapApiClient(this, mCurrentPosition);
        String url = googleMapiApiClient.getRequestUrl();
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url,
                null, this, this
        ));
    }
    @Override
    public void onResponse(JSONObject response) {
        try {
            GoogleMapTextSearchApiResult apiResult = new GoogleMapTextSearchApiResult(response);
            if (apiResult.resultCount() == 0) {
                Toast.makeText(this, getString(R.string.info_not_found), Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, getString(R.string.info_load_completed), Toast.LENGTH_LONG).show();
            for (int i = 0; i < apiResult.resultCount(); i++) {
                String name = apiResult.getName(i);
                Double lat  = apiResult.getLat(i);
                Double lng  = apiResult.getLng(i);
                mMapOperator.addMarkerToMap(name, lat, lng);
            }

        } catch (JSONException e ) {
            Log.e(TAG, "Data parse error");
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Data load error");
        error.printStackTrace();
    }

    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mMapFragment.setVisibility(show ? View.GONE : View.VISIBLE);
        mMapFragment.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMapFragment.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}

