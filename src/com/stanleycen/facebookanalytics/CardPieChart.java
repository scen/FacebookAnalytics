package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stanleycen.facebookanalytics.graph.PieGraph;
import com.stanleycen.facebookanalytics.graph.PieSlice;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.ArrayList;

/**
 * Created by scen on 8/31/13.
 */
public class CardPieChart implements CardItem {
    public final String title;
    private int viewType;
    private ArrayList<PieSlice> slices;
    private boolean shouldCacheToBitmap;

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
        holder.pieChart.setSlices(slices);
        holder.pieChart.setShouldCacheToBitmap(isShouldCacheToBitmap());

        return v;
    }

    public ArrayList<PieSlice> getSlices() {
        return slices;
    }

    public void setSlices(ArrayList<PieSlice> slices) {
        this.slices = slices;
    }

    public boolean isShouldCacheToBitmap() {
        return shouldCacheToBitmap;
    }

    public void setShouldCacheToBitmap(boolean shouldCacheToBitmap) {
        this.shouldCacheToBitmap = shouldCacheToBitmap;
    }

    private class CardPieChartHolder {
        TextView title;
        PieGraph pieChart;
    }
}