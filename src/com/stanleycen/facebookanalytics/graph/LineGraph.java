package com.stanleycen.facebookanalytics.graph;

/**
 * Created by scen on 9/1/13.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stanleycen.facebookanalytics.R;
import com.stanleycen.facebookanalytics.Typefaces;
import com.stanleycen.facebookanalytics.Util;

import java.util.ArrayList;

public class LineGraph extends View {

    private ArrayList<Line> lines = new ArrayList<Line>();
    private Paint paint = new Paint();
    private Paint ylabelPaint = new Paint();
    private Paint xaxisLabelPaint = new Paint();
    private float minY = 0, minX = 0;
    private float maxY = 0, maxX = 0;
    private boolean isMaxYUserSet = false;
    private int lineToFill = -1;
    private OnPointClickedListener listener;
    private Bitmap fullImage;
    private boolean shouldUpdate = false;
    private LabelFormatter xlabelFormatter;
    private LabelFormatter ylabelFormatter;
    private int numHorizontalGrids;
    private int numVerticalGrids;
    Rect tmpRect = new Rect();

    public LineGraph(Context context) {
        this(context, null);
    }

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        ylabelPaint.setColor(Color.BLACK);
        ylabelPaint.setAlpha(110);
        ylabelPaint.setTextSize(Util.dipToPixels(getContext(), 10));
        ylabelPaint.setTypeface(Typefaces.get("sans-serif-thin", Typeface.NORMAL));
        ylabelPaint.setAntiAlias(true);

        xaxisLabelPaint.setColor(Color.BLACK);
        xaxisLabelPaint.setTextSize(Util.dipToPixels(getContext(), 12));
        xaxisLabelPaint.setTypeface(Typefaces.get("sans-serif-thin", Typeface.NORMAL));
        xaxisLabelPaint.setAntiAlias(true);
        xaxisLabelPaint.setAlpha(200);
        xaxisLabelPaint.setTextAlign(Paint.Align.CENTER);
    }



    public void setTextColor(int color) {
        ylabelPaint.setColor(color);
    }

    public void setTextSize(float s) {
        ylabelPaint.setTextSize(s);
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void update() {
        shouldUpdate = true;
        postInvalidate();
    }

    public void removeAllLines() {
        while (lines.size() > 0) {
            lines.remove(0);
        }
        shouldUpdate = true;
        postInvalidate();
    }

    public void addLine(Line line) {
        lines.add(line);
        shouldUpdate = true;
        postInvalidate();
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLineToFill(int indexOfLine) {
        this.lineToFill = indexOfLine;
        shouldUpdate = true;
        postInvalidate();
    }

    public int getLineToFill() {
        return lineToFill;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public Line getLine(int index) {
        return lines.get(index);
    }

    public int getSize() {
        return lines.size();
    }

    public void setRangeY(float min, float max) {
        minY = min;
        maxY = max;
        isMaxYUserSet = true;
    }

    public float getMaxY() {
        if (isMaxYUserSet) {
            return maxY;
        } else {
            maxY = lines.get(0).getPoint(0).getY();
            for (Line line : lines) {
                for (LinePoint point : line.getPoints()) {
                    if (point.getY() > maxY) {
                        maxY = point.getY();
                    }
                }
            }
            return maxY;
        }

    }

    public float getMinY() {
        if (isMaxYUserSet) {
            return minY;
        } else {
            float min = lines.get(0).getPoint(0).getY();
            for (Line line : lines) {
                for (LinePoint point : line.getPoints()) {
                    if (point.getY() < min) min = point.getY();
                }
            }
            minY = min;
            return minY;
        }
    }

    public float getMaxX() {
        float max = lines.get(0).getPoint(0).getX();
        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                if (point.getX() > max) max = point.getX();
            }
        }
        maxX = max;
        return maxX;

    }

    public float getMinX() {
        float max = lines.get(0).getPoint(0).getX();
        for (Line line : lines) {
            for (LinePoint point : line.getPoints()) {
                if (point.getX() < max) max = point.getX();
            }
        }
        maxX = max;
        return maxX;
    }

    public void onDraw(Canvas ca) {
        if (fullImage == null || shouldUpdate) {
            if (fullImage != null) {
                fullImage.recycle();
            }
            fullImage = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(fullImage);

            drawToCanvas(canvas);

            shouldUpdate = false;
        }

        ca.drawBitmap(fullImage, 0, 0, null);


    }

    private void drawToCanvas(Canvas canvas) {
//        canvas.drawText("1400 messages", getWidth() / 2, getHeight() / 2, ylabelPaint);
        paint.reset();

        boolean drawLegend = false;//lines.size() > 1;

        final float ylabelFontSpacing = ylabelPaint.getFontSpacing() + ylabelPaint.descent() + Util.dipToPixels(getContext(), 1);
        final float xaxislabelFontSpacing = xaxisLabelPaint.descent() - xaxisLabelPaint.ascent();// xaxisLabelPaint.getFontSpacing();

        final float legendSize = drawLegend ? Util.dipToPixels(getContext(), 20) : 0;

        final float bottomPadding = xaxislabelFontSpacing + Util.dipToPixels(getContext(), 2);
        final float leftPadding = 0;//Util.dipToPixels(getContext(), 10);
        final float rightPadding = 0;//Util.dipToPixels(getContext(), 10);
        final float topPadding = legendSize;


        final float drawableHeight = getHeight() - bottomPadding - ylabelFontSpacing - legendSize;
        final float drawableWidth = getWidth() - leftPadding - rightPadding;

        final float xaxisY = getHeight() - bottomPadding;

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setAlpha(80);
        paint.setAntiAlias(true);

        //bottom grid
        canvas.drawLine(leftPadding, xaxisY, getWidth() - rightPadding, xaxisY, paint);



        // Render grids
        paint.setStrokeWidth(1);
        paint.setAlpha(50);

        //left grid
        canvas.drawLine(leftPadding, xaxisY, leftPadding, xaxisY - drawableHeight, paint);

        int totHorizontalLines = getNumHorizontalGrids();
        for (int i = 0; i < totHorizontalLines; i++) {
            float ypos = xaxisY - ((float)(i + 1) * drawableHeight / (float)totHorizontalLines);
            canvas.drawLine(leftPadding, ypos, getWidth() - rightPadding, ypos, paint);
            canvas.drawText(getYlabelFormatter().format(i, totHorizontalLines, getMinX(), getMaxX()), leftPadding, ypos - ylabelPaint.descent() - 1, ylabelPaint);
        }

        for (int i = 0; i < getNumVerticalGrids(); i++) {
            float xpos = leftPadding + ((float)(i + 1) * drawableWidth / (float)getNumVerticalGrids());
            canvas.drawLine(xpos, xaxisY, xpos, xaxisY - drawableHeight, paint);
            if (i != getNumVerticalGrids() - 1) canvas.drawText(getXlabelFormatter().format(i, getNumVerticalGrids(), getMinY(), getMaxY()), xpos, xaxisY + (ylabelFontSpacing / 2), xaxisLabelPaint);
        }



        //to help with debugging formatting
//        {
//            Paint paint = new Paint();
//            paint.setColor(Color.RED);
//            paint.setStrokeWidth(1);
//            paint.setAntiAlias(true);
//            canvas.drawLine(0, 0, getWidth(), 0, paint);
//            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);
//            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paint);
//            canvas.drawLine(0, 0, 0, getHeight(), paint);
//            paint.setColor(Color.BLUE);
//            canvas.drawLine(0, legendSize, getWidth(), legendSize, paint);
//        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        Point point = new Point();
//        point.x = (int) event.getX();
//        point.y = (int) event.getY();
//
//        int count = 0;
//        int lineCount = 0;
//        int pointCount;
//
//        Region r = new Region();
//        for (Line line : lines) {
//            pointCount = 0;
//            for (LinePoint p : line.getPoints()) {
//
//                if (p.getPath() != null && p.getRegion() != null) {
//                    r.setPath(p.getPath(), p.getRegion());
//                    if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN) {
//                        indexSelected = count;
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        if (r.contains(point.x, point.y) && listener != null) {
//                            listener.onClick(lineCount, pointCount);
//                        }
//                        indexSelected = -1;
//                    }
//                }
//
//                pointCount++;
//                count++;
//            }
//            lineCount++;
//
//        }
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
//            shouldUpdate = true;
//            postInvalidate();
//        }

        return true;
    }

    public void setOnPointClickedListener(OnPointClickedListener listener) {
        this.listener = listener;
    }

    public LabelFormatter getXlabelFormatter() {
        return xlabelFormatter;
    }

    public void setXlabelFormatter(LabelFormatter xlabelFormatter) {
        this.xlabelFormatter = xlabelFormatter;
    }

    public int getNumHorizontalGrids() {
        return numHorizontalGrids;
    }

    public void setNumHorizontalGrids(int numHorizontalGrids) {
        this.numHorizontalGrids = numHorizontalGrids;
    }

    public LabelFormatter getYlabelFormatter() {
        return ylabelFormatter;
    }

    public void setYlabelFormatter(LabelFormatter ylabelFormatter) {
        this.ylabelFormatter = ylabelFormatter;
    }

    public int getNumVerticalGrids() {
        return numVerticalGrids;
    }

    public void setNumVerticalGrids(int numVerticalGrids) {
        this.numVerticalGrids = numVerticalGrids;
    }

    public interface OnPointClickedListener {
        abstract void onClick(int lineIndex, int pointIndex);
    }

    public abstract interface LabelFormatter {
        abstract String format(int idx, int tot, float min, float max);
    }
}
