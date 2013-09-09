package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by scen on 8/28/13.
 */
public class CardDateTime implements CardItem {
    private final String title;
    private final String date;
    private final String time;
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public CardDateTime(int viewType, String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.viewType = viewType;
        this.time = time;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardDateTimeHolder holder = new CardDateTimeHolder();

        if (v == null) {
            v = (View) inflater.inflate(R.layout.card_date_time, null);

            TextView titleView = (TextView) v.findViewById(R.id.title);
            TextView dateView = (TextView) v.findViewById(R.id.date);
            TextView timeView = (TextView) v.findViewById(R.id.time);

            holder.titleView = titleView;
            holder.dateView = dateView;
            holder.timeView = timeView;

            v.setTag(holder);
        } else {
            holder = (CardDateTimeHolder) v.getTag();
        }
        holder.titleView.setText(title);
        holder.dateView.setText(date);
        holder.timeView.setText(time);

        return v;
    }

    private class CardDateTimeHolder {
        public TextView titleView;
        public TextView dateView;
        public TextView timeView;
    }
}
