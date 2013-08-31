package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by scen on 8/31/13.
 */
public class CardPieChart implements CardItem {
    public final String title;
    private int viewType;

    public int getViewType() {
        return viewType;
    }
    public CardPieChart(int viewType, String title) {
        this.viewType = viewType;
        this.title = title;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardPieChartHolder holder = new CardPieChartHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_pie_chart, null);


            holder.title = (TextView)v.findViewById(R.id.title);
            holder.pieChart = (PieGraph)v.findViewById(R.id.pie);
            v.setTag(holder);
        }
        else {
            holder = (CardPieChartHolder)v.getTag();
        }

        holder.title.setText(this.title);

        PieSlice slice = new PieSlice();
        slice.setColor(Color.parseColor("#99CC00"));
        slice.setValue(2);
        holder.pieChart.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#FFBB33"));
        slice.setValue(3);
        holder.pieChart.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#AA66CC"));
        slice.setValue(8);
        holder.pieChart.addSlice(slice);

        return v;
    }

    private class CardPieChartHolder {
        TextView title;
        PieGraph pieChart;
    }
}