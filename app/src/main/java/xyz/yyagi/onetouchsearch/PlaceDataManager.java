package xyz.yyagi.onetouchsearch;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import xyz.yyagi.onetouchsearch.models.Place;

/**
 * Created by yaginuma on 14/10/17.
 */
public class PlaceDataManager {
    private Context mContext;
    private final static String PLACE_DATA_FILE_NAME = "place_data";
    private static final String TAG = PlaceDataManager.class.getSimpleName();
    private ArrayList<Place> mPlaceData;

    public PlaceDataManager(Context context) {
        this.mContext = context;
        this.mPlaceData = new ArrayList<Place>();
        read();
    }

    private void read() {
        FileInputStream in = null;
        try {
            in = mContext.openFileInput(PLACE_DATA_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            ObjectInputStream is = new ObjectInputStream(in);
            mPlaceData = (ArrayList<Place>) is.readObject();
            in.close();
            mContext.deleteFile(PLACE_DATA_FILE_NAME);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File Not Exist");
            if(in != null) try{ in.close(); } catch(Exception ignore){}
        } catch (Exception e) {
            Log.e(TAG, "File Read Error");
            e.printStackTrace();
            if(in != null) try{ in.close(); } catch(Exception ignore){}
        }
    }

    public void save() {
        FileOutputStream out = null;
        try {
            out = mContext.openFileOutput(PLACE_DATA_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(this.mPlaceData);
            out.close();
        } catch (Exception e) {
            if(out != null) try{ out.close(); } catch(Exception ignore){}
        }
    }

    public void delete() {
        mContext.deleteFile(PLACE_DATA_FILE_NAME);
    }

    public void add(Place place) {
        this.mPlaceData.add(place);
    }

    public boolean hasPlaceData() {
        return !this.mPlaceData.isEmpty();
    }

    public ArrayList<Place> get() {
        return mPlaceData;
    }

    public void clear() {
        this.mPlaceData.clear();
    }
}
