package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by scen on 8/28/13.
 */

public class CardAdapter extends com.haarman.listviewanimations.ArrayAdapter<CardItem> {
    public LayoutInflater mInflater;
    private Context context;
    private int mNumTypes;

    public CardAdapter(Context context, List<CardItem> items, int numTypes) {
        super(items);
        mInflater = LayoutInflater.from(context);
        this.mNumTypes = numTypes;
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return mNumTypes;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView, position, context);
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }

}
