package xyz.yyagi.onetouchsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yaginuma on 14/08/20.
 */
public class GoogleMapApiClient {
    private Context mContext;
    private Position mPosition;
    private String mGooglePlaceAPIKey;
    private String mSearchWord;

    private static final String URL_BASE =
            "https://maps.googleapis.com/maps/api/place/textsearch/json?radius=500&sensor=true&language=ja";

    public GoogleMapApiClient(Context context, Position position) {
        this.mContext = context;
        this.mPosition = position;
        this.mGooglePlaceAPIKey = context.getString(R.string.google_place_api_key);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.mSearchWord = sharedPreferences.getString(mContext.getString(R.string.pref_search_word_key), "");

    }

    public String getRequestUrl() {
        String location = "&location=" + mPosition.getLat() + "," + mPosition.getLng();
        String url = URL_BASE + location + "&query=" + Util.encode(this.mSearchWord) + "&key=" + mGooglePlaceAPIKey;
        return url;
    }
}
