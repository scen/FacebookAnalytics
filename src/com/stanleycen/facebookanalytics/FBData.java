package com.stanleycen.facebookanalytics;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by scen on 8/29/13.
 */
public class FBData {
    public DateTime lastUpdate = null;
    public CollectionMethod collectionMethod = CollectionMethod.UNIFIED_API;

    public ArrayList<FBThread> threads = new ArrayList<FBThread>();
    public HashMap<String, FBUser> userMap = new HashMap<String, FBUser>();

    public enum CollectionMethod {
        OLD_API,
        UNIFIED_API
    }
}
