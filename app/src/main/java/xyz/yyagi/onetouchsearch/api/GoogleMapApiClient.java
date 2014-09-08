package xyz.yyagi.onetouchsearch.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import xyz.yyagi.onetouchsearch.Position;
import xyz.yyagi.onetouchsearch.R;
import xyz.yyagi.onetouchsearch.Util;

/**
 * Created by yaginuma on 14/08/20.
 */
public class GoogleMapApiClient {
    private Context mContext;
    private Position mPosition;
    private String mGooglePlaceAPIKey;
    private ArrayList<String> mSearchWordList;
    private List<Float> mColorList;
    private SharedPreferences mSharedPrefereces;

    private static final String URL_BASE =
            "https://maps.googleapis.com/maps/api/place/textsearch/json?radius=500&sensor=true&language=ja";

    public GoogleMapApiClient(Context context, Position position) {
        this.mContext = context;
        this.mPosition = position;
        this.mGooglePlaceAPIKey = context.getString(R.string.google_place_api_key);
        this.mSearchWordList = new ArrayList<String>();
        this.mColorList = Arrays.asList(BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_AZURE);

        this.mSharedPrefereces= PreferenceManager.getDefaultSharedPreferences(mContext);
        setup();
    }

    public ArrayList getSearchWordList() {
        return this.mSearchWordList;
    }

    public float getIconColor(int index) {
        return this.mColorList.get(index);
    }

    public void setup() {
        mSearchWordList.clear();

        String searhWord = mSharedPrefereces.getString(mContext.getString(R.string.pref_search_word1_key), "");
        if (!TextUtils.isEmpty(searhWord)) {
            mSearchWordList.add(searhWord);
        }

        searhWord = mSharedPrefereces.getString(mContext.getString(R.string.pref_search_word2_key), "");
        if (!TextUtils.isEmpty(searhWord)) {
            mSearchWordList.add(searhWord);
        }
    }

    public ArrayList<String> getRequestUrlList() {
        String location = "&location=" + mPosition.getLat() + "," + mPosition.getLng();
        String base = URL_BASE + location + "&key=" + mGooglePlaceAPIKey;
        ArrayList requestUrlList = new ArrayList<String>();

        for(String searchWord: mSearchWordList) {
            requestUrlList.add(base + "&query=" + Util.encode(searchWord));
        }
        return requestUrlList;
    }
}
