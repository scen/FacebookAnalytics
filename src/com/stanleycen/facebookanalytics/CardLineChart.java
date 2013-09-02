package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.stanleycen.facebookanalytics.graph.Line;
import com.stanleycen.facebookanalytics.graph.LineGraph;
import com.stanleycen.facebookanalytics.graph.LinePoint;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by scen on 8/31/13.
 */
public class CardLineChart implements CardItem {
    public final String title;
    private int viewType;

    private ArrayList<Line> lines;
    private LineGraph.LabelFormatter xFormatter;
    private LineGraph.LabelFormatter yFormatter;
    private float minY = Float.MAX_VALUE;
    private float maxY = Float.MAX_VALUE;
    private int numHorizontalGrids = 5;
    private int numVerticalGrids = 6;
    private boolean shouldCacheToBitmap;

    public int getViewType() {
        return viewType;
    }
    public CardLineChart(int viewType, String title) {
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

        CardLineChartHolder holder = new CardLineChartHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_line_chart, null);

            holder.title = (TextView)v.findViewById(R.id.title);
            holder.lineChart = (LineGraph)v.findViewById(R.id.linechart);
            v.setTag(holder);
        }
        else {
            holder = (CardLineChartHolder)v.getTag();
        }

        holder.lineChart.setLines(lines);
        holder.lineChart.setNumVerticalGrids(getNumVerticalGrids());
        holder.lineChart.setNumHorizontalGrids(getNumHorizontalGrids());
        holder.lineChart.setXlabelFormatter(getxFormatter());
        holder.lineChart.setYlabelFormatter(getyFormatter());
        holder.lineChart.setShouldCacheToBitmap(isShouldCacheToBitmap());
        if (minY != Float.MAX_VALUE) {
            holder.lineChart.setRangeY(minY, maxY);
        }

        holder.title.setText(this.title);

        return v;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public LineGraph.LabelFormatter getxFormatter() {
        return xFormatter;
    }

    public void setxFormatter(LineGraph.LabelFormatter xFormatter) {
        this.xFormatter = xFormatter;
    }

    public LineGraph.LabelFormatter getyFormatter() {
        return yFormatter;
    }

    public void setyFormatter(LineGraph.LabelFormatter yFormatter) {
        this.yFormatter = yFormatter;
    }

    public void setRangeY(float min, float max) {
        this.minY = min;
        this.maxY = max;
    }

    public int getNumHorizontalGrids() {
        return numHorizontalGrids;
    }

    public void setNumHorizontalGrids(int numHorizontalGrids) {
        this.numHorizontalGrids = numHorizontalGrids;
    }

    public int getNumVerticalGrids() {
        return numVerticalGrids;
    }

    public void setNumVerticalGrids(int numVerticalGrids) {
        this.numVerticalGrids = numVerticalGrids;
    }

    public boolean isShouldCacheToBitmap() {
        return shouldCacheToBitmap;
    }

    public void setShouldCacheToBitmap(boolean shouldCacheToBitmap) {
        this.shouldCacheToBitmap = shouldCacheToBitmap;
    }

    private class CardLineChartHolder {
        TextView title;
        LineGraph lineChart;
    }
}
