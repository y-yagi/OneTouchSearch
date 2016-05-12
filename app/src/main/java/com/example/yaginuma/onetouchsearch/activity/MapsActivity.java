package com.example.yaginuma.onetouchsearch.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yaginuma.onetouchsearch.R;
import com.example.yaginuma.onetouchsearch.api.GoogleMapTextSearchApiResult;
import com.example.yaginuma.onetouchsearch.api.GooglePlaceAPIClient;
import com.example.yaginuma.onetouchsearch.model.GoogleMapOperator;
import com.example.yaginuma.onetouchsearch.model.Position;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleMapOperator mMapOperator;
    private int mRequestCount = 0;
    private ArrayList<String> mSearchWordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchWordList = new ArrayList<>();
        SharedPreferences sharedPrefereces= PreferenceManager.getDefaultSharedPreferences(this);
        mSearchWordList.add(sharedPrefereces.getString(getString(R.string.pref_search_word1_key), ""));
        if (mSearchWordList.get(0).isEmpty())  {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
            return;
        }
        mSearchWordList.add(sharedPrefereces.getString(getString(R.string.pref_search_word2_key), ""));

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

        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
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
                                mMapOperator.getIcon(mRequestCount)
                            );
                            mRequestCount++;

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
        };

        Call<ResponseBody> searchCall1 = googlePlaceApiClient.textserach(mSearchWordList.get(0), position.toString());
        searchCall1.enqueue(callback);

        // NOTE: Currently, search words support only 2 words. If need support more words, this operation extra to method.
        if (!mSearchWordList.get(1).isEmpty()) {
            Call<ResponseBody> searchCall2 = googlePlaceApiClient.textserach(mSearchWordList.get(1), position.toString());
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
}
