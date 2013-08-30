package com.stanleycen.facebookanalytics;

import android.app.Application;
import android.util.Log;

/**
 * Created by scen on 8/29/13.
 */
public class GlobalApp extends Application {
    public FBAccount fb;
    public DatabaseHandler db;

    public UpdateState updateState = new UpdateState();


    private static GlobalApp _inst;

    public GlobalApp() {
        super();
//        System.setProperty("org.joda.time.DateTimeZone.Provider",
//                FastDateTimeZoneProvider.class.getCanonicalName());
        Log.d("GA", FastDateTimeZoneProvider.class.getCanonicalName());
        _inst = this;
    }

    public static GlobalApp get() {
        return _inst;
    }

    public class UpdateState {
        boolean updating = false;
        DataDownloaderService.ProgressBarUpdate pbu;
        CardUpdate.CardUpdateHolder holder;
    }
}
