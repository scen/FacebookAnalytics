package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by scen on 8/29/13.
 */
public class FBData {
    public Calendar lastUpdate = null;
    public CollectionMethod collectionMethod;

    public ArrayList<FBThread> threads = new ArrayList<FBThread>();
    public HashMap<String, FBUser> userMap = new HashMap<String, FBUser>();

    public enum CollectionMethod {
        OLD_API,
        UNIFIED_API
    }
}
