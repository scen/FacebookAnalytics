package com.stanleycen.facebookanalytics;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by scen on 8/29/13.
 */
public class FBThread {
    public String id;
    public String title;
    public DateTime lastUpdate;
    public boolean isGroupConversation;
    public HashSet<FBUser> participants = new HashSet<FBUser>();

    public ArrayList<FBMessage> messages = new ArrayList<FBMessage>();
    public int messageCount;

    // Statistics computed post-load
    public int charCount;
    FBUser other;
    HashMap<FBUser, MutableInt> msgCount;
}
