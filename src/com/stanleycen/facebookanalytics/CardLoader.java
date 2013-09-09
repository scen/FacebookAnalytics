package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by scen on 8/31/13.
 */
public class CardLoader implements CardItem {
    private final String msg;
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public CardLoader(int viewType, String msg) {
        this.viewType = viewType;
        this.msg = msg;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardLoaderHolder holder = new CardLoaderHolder();

        if (v == null) {
            v = (View) inflater.inflate(R.layout.card_loader, null);


            holder.bar = (ProgressBar) v.findViewById(R.id.progressBar);
            holder.message = (TextView) v.findViewById(R.id.status);

            v.setTag(holder);
        } else {
            holder = (CardLoaderHolder) v.getTag();
        }

        holder.message.setText(msg);

        return v;
    }

    private class CardLoaderHolder {
        public ProgressBar bar;
        public TextView message;

    }
}