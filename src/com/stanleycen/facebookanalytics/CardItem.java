package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by scen on 8/28/13.
 */
public abstract class CardItem {
    boolean animated = false;

    public abstract int getViewType();
    public abstract View getView(LayoutInflater inflater, View convertView, int position, Context context);
}