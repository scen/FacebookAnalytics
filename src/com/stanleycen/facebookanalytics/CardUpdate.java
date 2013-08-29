package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class CardUpdate extends CardItem {
    private int viewType;

    public int getViewType() {
        return viewType;
    }
    public CardUpdate(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardUpdateHolder holder = new CardUpdateHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_update, null);

            v.setTag(holder);
        }
        else {
            holder = (CardUpdateHolder)v.getTag();
        }

        return v;
    }

    private class CardUpdateHolder {
        public TextView titleView;
        public TextView dateView;
        public TextView timeView;
    }
}