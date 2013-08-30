package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by scen on 8/29/13.
 */
public class FBData {
    public Calendar lastUpdate;
    public CollectionMethod collectionMethod;

    public ArrayList<FBThread> threads;
    public HashMap<String, FBUser> userMap;

    public enum CollectionMethod {
        OLD_API,
        UNIFIED_API
    }
}
