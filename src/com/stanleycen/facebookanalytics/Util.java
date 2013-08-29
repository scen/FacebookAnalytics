package com.stanleycen.facebookanalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by scen on 8/29/13.
 */
public class Util {
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
    private static final SimpleDateFormat timeTZFormatter = new SimpleDateFormat("hh:mm:ss a z");

    public static String getDate(Calendar gc) {
//        dateFormatter.setCalendar(gc);
        return dateFormatter.format(gc.getTime());
    }

    public static String getTimeWithTZ(Calendar gc) {
//        dateFormatter.setCalendar(gc);
        return timeTZFormatter.format(gc.getTime());
    }
}
