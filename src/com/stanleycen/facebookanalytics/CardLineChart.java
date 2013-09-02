package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.stanleycen.facebookanalytics.graph.Line;
import com.stanleycen.facebookanalytics.graph.LineGraph;
import com.stanleycen.facebookanalytics.graph.LinePoint;

import java.util.ArrayList;

/**
 * Created by scen on 8/31/13.
 */
public class CardLineChart implements CardItem {
    public final String title;
    private int viewType;

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


        Line l = new Line();
        for (int i = 0; i < 10; i++) {
            l.addPoint(new LinePoint(i, i));
        }
        l.setColor(Color.parseColor("#FFBB33"));

        holder.lineChart.addLine(l);
        holder.lineChart.setRangeY(0, 10);
        holder.lineChart.setNumHorizontalGrids(2);
        holder.lineChart.setNumVerticalGrids(2);
        holder.lineChart.setYlabelFormatter(new LineGraph.LabelFormatter() {
            @Override
            public String format(int cur, int tot) {
                return "1400" + (cur == tot - 1 ? " messages" : "");
            }
        });
        holder.lineChart.setXlabelFormatter(new LineGraph.LabelFormatter() {
            @Override
            public String format(int idx, int tot) {
                return "12 AM";
            }
        });
        holder.title.setText(this.title);

        return v;
    }

    private class CardLineChartHolder {
        TextView title;
        LineGraph lineChart;
    }
}
