/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.stanleycen.facebookanalytics.graph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.stanleycen.facebookanalytics.R;
import com.stanleycen.facebookanalytics.Typefaces;
import com.stanleycen.facebookanalytics.Util;

import java.util.ArrayList;

public class PieGraph extends View {

    private ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
    private Paint paint = new Paint();

    private int thickness;
    private OnSliceClickedListener listener;

    private static final float PADDING = 2;
    private float fontSpacing = 0;
    private final float spacingBetweenLegendText = Util.dipToPixels(getContext(), 8);
    private final float spacingBetweenLegendAndPie = Util.dipToPixels(getContext(), 10);

    public PieGraph(Context context) {
        super(context);
    }

    public PieGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int heightPie = widthSize / 2;

        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(Util.dipToPixels(getContext(), 14));
        paint.setTypeface(Typefaces.get("sans-serif-thin", Typeface.NORMAL));
        paint.setAntiAlias(true);

        fontSpacing = paint.getFontSpacing();
        int totalLegendHeight = (int) (slices.size() * fontSpacing) +
                (int)((slices.size() - 1) * spacingBetweenLegendText);

        setMeasuredDimension(widthSize, Math.max(heightPie, totalLegendHeight));
    }

    @Override
    public void onDraw(Canvas canvas) {
        Bitmap cache = getDrawingCache();
        if (cache != null) {
            paint.reset();
            Log.w("cache", "fromc ache");
            canvas.drawBitmap(cache, 0, 0, paint);
            return;
        }
        canvas.drawColor(Color.TRANSPARENT);
        paint.reset();
        paint.setAntiAlias(true);
        float midX, midY, radius, innerRadius;

        float currentAngle = 270;
        float currentSweep;
        int totalValue = 0;


        midX = getWidth() / 4;
        midY = getHeight() / 2;

        radius = getWidth() / 4;
        thickness = (int)radius / 4;
        radius -= PADDING;
        innerRadius = radius - thickness;

        for (PieSlice slice : slices) {
            totalValue += slice.getValue();
        }

        int count = 0;
        for (PieSlice slice : slices) {
            paint.setColor(slice.getColor());
            currentSweep = (slice.getValue() / totalValue) * (360);

            if (slice.getPath() == null) {
                Path p = new Path();
                p.arcTo(new RectF(midX - radius, midY - radius, midX + radius, midY + radius), currentAngle + PADDING, currentSweep - PADDING);
                p.arcTo(new RectF(midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius), (currentAngle + PADDING) + (currentSweep - PADDING), -(currentSweep - PADDING));
                p.close();
                slice.setPath(p);
            }
            canvas.drawPath(slice.getPath(), paint);
            paint.setColor(Util.getStrokeColor(slice.getColor()));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            canvas.drawPath(slice.getPath(), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);
            paint.setColor(slice.getColor());

            currentAngle = currentAngle + currentSweep;

            count++;
        }
        paint.reset();
        paint.setTextSize(Util.dipToPixels(getContext(), 14));
        paint.setTypeface(Typefaces.get("sans-serif-thin", Typeface.NORMAL));
        paint.setAntiAlias(true);
//        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paint);

        float k = (paint.descent() - paint.ascent()) / 2;

        int totalLegendHeight = (int) ((slices.size()) * fontSpacing) +
                (int)((slices.size() - 1) * spacingBetweenLegendText) - (int)k;

        float rx = midX + radius + spacingBetweenLegendAndPie;
        float ry = (getHeight() / 2) - (totalLegendHeight / 2) + (fontSpacing / 2);

        for (PieSlice slice : slices) {
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);
            paint.setColor(slice.getColor());
            canvas.drawRect(rx, ry - k, rx + (fontSpacing / 2), ry + (fontSpacing / 2) - k, paint);
            paint.setColor(getResources().getColor(R.color.card_header));
            canvas.drawText(slice.getTitle(), rx + (fontSpacing), ry, paint);
            paint.setColor(Util.getStrokeColor(slice.getColor()));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            canvas.drawRect(rx, ry - k, rx + (fontSpacing / 2), ry + (fontSpacing / 2) - k, paint);
            ry += fontSpacing + spacingBetweenLegendText;
        }
        setDrawingCacheEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }

    public ArrayList<PieSlice> getSlices() {
        return slices;
    }

    public void setSlices(ArrayList<PieSlice> slices) {
        this.slices = slices;
        postInvalidate();
    }

    public PieSlice getSlice(int index) {
        return slices.get(index);
    }

    public void addSlice(PieSlice slice) {
        this.slices.add(slice);
        postInvalidate();
    }

    public void setOnSliceClickedListener(OnSliceClickedListener listener) {
        this.listener = listener;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        postInvalidate();
    }

    public void removeSlices() {
        for (int i = slices.size() - 1; i >= 0; i--) {
            slices.remove(i);
        }
        postInvalidate();
    }

    public static interface OnSliceClickedListener {
        public abstract void onClick(int index);
    }

}
