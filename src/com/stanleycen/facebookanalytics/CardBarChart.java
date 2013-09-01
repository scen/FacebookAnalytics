package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.stanleycen.facebookanalytics.graph.Bar;
import com.stanleycen.facebookanalytics.graph.BarGraph;
import com.stanleycen.facebookanalytics.graph.PieSlice;

import java.util.ArrayList;

/**
 * Created by scen on 9/1/13.
 */
public class CardBarChart implements CardItem {
    public final String title;
    private int viewType;
    private ArrayList<Bar> bars;

    public int getViewType() {
        return viewType;
    }
    public CardBarChart(int viewType, String title) {
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

        CardBarChartHolder holder = new CardBarChartHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_bar_graph, null);

            holder.title = (TextView)v.findViewById(R.id.title);
            holder.barChart = (BarGraph)v.findViewById(R.id.bar);
            v.setTag(holder);
        }
        else {
            holder = (CardBarChartHolder)v.getTag();
        }

        holder.title.setText(this.title);
        holder.barChart.setBars(bars);

        return v;
    }

    public ArrayList<Bar> getBars() {
        return bars;
    }

    public void setBars(ArrayList<Bar> bars) {
        this.bars = bars;
    }

    private class CardBarChartHolder {
        TextView title;
        BarGraph barChart;
    }
}