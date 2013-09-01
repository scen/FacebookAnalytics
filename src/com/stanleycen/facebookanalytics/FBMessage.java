package com.stanleycen.facebookanalytics;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by scen on 8/29/13.
 */
public class FBMessage {
    public String body;
    public DateTime timestamp;
    public FBUser from;
    public String id;

    public Source source;

    public String thread;

    public boolean hasCoordinates;
    public float latitude;
    public float longitude;

    public ArrayList<FBAttachment> attachments = new ArrayList<FBAttachment>();

    enum Source {
        WEB,
        MOBILE,
        OTHER
    }
}
