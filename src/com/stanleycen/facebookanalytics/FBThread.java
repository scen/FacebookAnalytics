package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by scen on 8/29/13.
 */
public class FBThread {
    public String id;
    public String title;
    public Calendar lastUpdate;
    public ArrayList<String> participants;

    public ArrayList<FBMessage> messages;
    public int messageCount;
}
