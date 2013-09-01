package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by scen on 8/31/13.
 */
public class Typefaces {
    private static final String TAG = "Typefaces";

//    private static final HashMap<String, Typeface> cach e = new HashMap<String, Typeface>();
    private static final HashMap<String, Typeface[]> cache = new HashMap<String, Typeface[]>();

    public static Typeface get(String name, int type) {
        if (!cache.containsKey(name)) {
            try {
                Typeface t = Typeface.create(name, type);
                Typeface val[] = new Typeface[4];
                val[type] = t;
                cache.put(name, val);
                return t;
            } catch (Exception e) {
                Log.e(TAG, "Could not get typeface '" + name
                        + "' because " + e.getMessage());
                return null;
            }
        }
        if (cache.get(name)[type] == null) {
            return cache.get(name)[type] = Typeface.create(name, type);
        }
        return cache.get(name)[type];
    }
}