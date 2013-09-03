package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.stanleycen.facebookanalytics.graph.Line;
import com.stanleycen.facebookanalytics.graph.LineGraph;

import java.util.ArrayList;

/**
 * Created by scen on 8/31/13.
 */
public class CardLineChartSpinner implements CardItem {
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
    boolean initClicked = false;
    public CardLineChartHolder holder;


    public int getViewType() {
        return viewType;
    }
    public CardLineChartSpinner(int viewType, String title) {
        this.viewType = viewType;
        this.title = title;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, final Context context) {
        View v = convertView;

        holder = new CardLineChartHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_line_chart_spinner, null);

            holder.title = (TextView)v.findViewById(R.id.title);
            holder.lineChart = (LineGraph)v.findViewById(R.id.linechart);
            holder.spinner = (Spinner)v.findViewById(R.id.spinner);
            v.setTag(holder);
        }
        else {
            holder = (CardLineChartHolder)v.getTag();
        }

        refreshLineChart();

        holder.title.setText(this.title);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(0xAA000000);
                ((TextView) adapterView.getChildAt(0)).setTypeface(Typefaces.get("sans-serif-light", Typeface.NORMAL));
                if (initClicked) {
                    Intent intent = new Intent("com.stanleycen.facebookanalytics.spinner.group");
                    intent.putExtra("title", title);
                    intent.putExtra("value", i);
                    context.sendBroadcast(intent);
                }
                initClicked = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return v;
    }

    public void refreshLineChart() {
        if (holder == null || holder.lineChart == null) return;
        holder.lineChart.setLines(lines);
        holder.lineChart.setNumVerticalGrids(getNumVerticalGrids());
        holder.lineChart.setNumHorizontalGrids(getNumHorizontalGrids());
        holder.lineChart.setXlabelFormatter(getxFormatter());
        holder.lineChart.setYlabelFormatter(getyFormatter());
        holder.lineChart.setShouldCacheToBitmap(isShouldCacheToBitmap());
        if (minY != Float.MAX_VALUE) {
            holder.lineChart.setRangeY(minY, maxY);
        }
        holder.lineChart.invalidate();
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
        Spinner spinner;
    }
}
