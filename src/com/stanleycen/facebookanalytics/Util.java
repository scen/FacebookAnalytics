package com.stanleycen.facebookanalytics;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

/**
 * Created by scen on 8/29/13.
 */
public class Util {
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy")
            .withZone(DateTimeZone.getDefault());
    private static final DateTimeFormatter timeTZFormatter = DateTimeFormat.forPattern("h:mm:ss a z")
            .withZone(DateTimeZone.getDefault());
    private static final DecimalFormat decimalFormat = new DecimalFormat();


    public static String getDate(DateTime dt) {
        return dateFormatter.print(dt);
    }

    public static String getTimeWithTZ(DateTime dt) {
        return timeTZFormatter.print(dt);
    }

    public static String getFormattedInt(int i) {
        return decimalFormat.format(i);
    }
}
