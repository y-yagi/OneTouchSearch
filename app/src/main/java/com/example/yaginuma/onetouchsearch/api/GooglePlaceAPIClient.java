package com.example.yaginuma.onetouchsearch.api;

import android.content.Context;

import com.example.yaginuma.onetouchsearch.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yaginuma on 16/05/11.
 */
public class GooglePlaceAPIClient {
    private GooglePlaceService mGooglePlaceService;
    private String mGooglePlaceAPIKey;

    public GooglePlaceAPIClient(Context context) {
        Retrofit builder = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .build();
        mGooglePlaceService = builder.create(GooglePlaceService.class);
        this.mGooglePlaceAPIKey = context.getString(R.string.places_search_api_key);
    }

    public interface GooglePlaceService {
        @GET("maps/api/place/textsearch/json?radius=500&sensor=true&language=ja")
        Call<ResponseBody> textsearch(@Query("query") String query, @Query("location") String location, @Query("key") String key);
    }

    public Call<ResponseBody> textserach(String query, String location) {
        return mGooglePlaceService.textsearch(query, location, mGooglePlaceAPIKey);
    }
}
