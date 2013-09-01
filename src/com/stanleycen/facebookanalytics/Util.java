package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by scen on 8/29/13.
 */
public class Util {
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMM dd, yyyy")
            .withZone(DateTimeZone.getDefault());
    private static final DateTimeFormatter timeTZFormatter = DateTimeFormat.forPattern("h:mm:ss a z")
            .withZone(DateTimeZone.getDefault());
    private static final DecimalFormat decimalFormat = new DecimalFormat();

    public static int[] colors = null;

    public static String getDate(DateTime dt) {
        return dateFormatter.print(dt);
    }

    public static String getTimeWithTZ(DateTime dt) {
        return timeTZFormatter.print(dt);
    }

    public static String getFormattedInt(int i) {
        return decimalFormat.format(i);
    }

    public static float getAttributeDimension(final Context context, final int resId)
    {
        return getAttributeDimension(context, context.getTheme(), resId);
    }

    public static final int getFirstDayOfWeek() {
        return ((Calendar.getInstance().getFirstDayOfWeek() + 5) % 7) + 1;
    }

    /**
     * Resolves an attribute of the theme and returns the attribute value as a dimension of the display.
     * <p>
     * For example, this method can resolve the resource ID <code>android.R.attr.listPreferredItemHeight</code> and return the value as a dimension to be used in
     * programmatically constructing a layout.
     * </p>
     * @param context The current context.
     * @param theme The theme for which an attribute should be resolved.
     * @param resid The resource identifier of the desired theme attribute.
     * @return The theme attribute as a display dimension.
     * @throws NotFoundException if the given resource is not found or is not of the appropriate type.
     * @see Resources#getDisplayMetrics()
     * @see Theme#resolveAttribute(int, TypedValue, boolean)
     */
    public static float getAttributeDimension(final Context context, final Resources.Theme theme, final int resId)
    {
        final TypedValue typedValue = new TypedValue(); //create a new typed value to received the resolved attribute value
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if(!theme.resolveAttribute(resId, typedValue, true)) //if we can't resolve the value
        {
            throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(resId));
        }
        if(typedValue.type != TypedValue.TYPE_DIMENSION) //if the value isn't of the correct type
        {
            throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(resId) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
        }
        return typedValue.getDimension(displayMetrics); //return the value of the attribute in terms of the display
    }

    /**
     * Returns the list preferred item height theme attribute as a dimension of the display.
     * @param context The current context.
     * @return The list preferred item height for the current context theme.
     * @throws NotFoundException if the given resource is not found or is not of the appropriate type.
     * @see <a href="http://stackoverflow.com/questions/5982132/android-how-to-get-value-of-listpreferreditemheight-attribute-in-code">Android: how to get value
     *      of “listPreferredItemHeight” attribute in code?</a>
     */
    public static float getListPreferredItemHeightDimension(final Context context)
    {
        return getAttributeDimension(context, android.R.attr.listPreferredItemHeight);
    }

    public static float dipToPixels(final Context context, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public static View getSeparatingHeaderView(final Context context, final LayoutInflater inflater, final ListView list) {
        View header = inflater.inflate(R.layout.listview_header_for_transparent_action_bar, list, false);
        header.setPadding(0, (int)Util.getAttributeDimension(context, android.R.attr.actionBarSize) - context.getResources().getDimensionPixelSize(R.dimen.half_card_sep_height), 0, 0);
        return header;
    }

    public static void addSeparatingHeaderView(final Context context, final LayoutInflater inflater, final ListView list) {
        list.addHeaderView(getSeparatingHeaderView(context, inflater, list));
    }

    public static int getStrokeColor(int i) {
        return Color.argb(255, (0xFF & i >> 16) * 8 / 10, (0xFF & i >> 8) * 8 / 10, (i & 0xFF) * 8 / 10);
    }
}
