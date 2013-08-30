package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by scen on 8/29/13.
 */
public class FBMessage {
    public String body;
    public Calendar timestamp = Calendar.getInstance();
    public String from;
    public String id;

    public boolean hasCoordinates;
    public float latitude;
    public float longitude;

    public ArrayList<FBAttachment> attachments = new ArrayList<FBAttachment>();
}