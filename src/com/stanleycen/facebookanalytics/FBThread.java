package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by scen on 8/29/13.
 */
public class FBThread {
    public String id;
    public String title;
    public Calendar lastUpdate = Calendar.getInstance();
    public boolean isGroupConversation;
    public HashSet<String> participants = new HashSet<String>();

    public ArrayList<FBMessage> messages = new ArrayList<FBMessage>();
    public int messageCount;
}
