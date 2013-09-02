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


//        Line l = new Line();
//        Line l2 = new Line();
//        for (int i = 0; i < 12; i++) {
//            l.addPoint(new LinePoint(i, i));
//            l2.addPoint(new LinePoint(i, 11 - i));
//        }
//        l.setColor(Util.colors[0]);
//        l2.setColor(Util.colors[1]);
//        l.setName("You");
//        l2.setName("Jacob");
//
//        holder.lineChart.addLine(l);
//        holder.lineChart.addLine(l2);
//        holder.lineChart.setRangeY(0, 12);
//        holder.lineChart.setNumHorizontalGrids(5);
//        holder.lineChart.setNumVerticalGrids(6);

        holder.lineChart.setLines(lines);

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

    private class CardLineChartHolder {
        TextView title;
        LineGraph lineChart;
    }
}
