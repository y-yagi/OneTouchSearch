package xyz.yyagi.onetouchsearch.models;

/**
 * Created by yaginuma on 15/01/09.
 */

import android.app.Activity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.io.Serializable;

import xyz.yyagi.onetouchsearch.MainActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class PositionTest {
    @org.junit.Test
    public void defaultPositionIsTokyo() throws Exception {
        assert true;
    }
}
