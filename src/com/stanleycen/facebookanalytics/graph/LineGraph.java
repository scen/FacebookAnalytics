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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
    public Bitmap fullImage;
    private boolean shouldUpdate = false;
    private LabelFormatter xlabelFormatter;
    private LabelFormatter ylabelFormatter;
    private int numHorizontalGrids;
    private int numVerticalGrids;
    Rect tmpRect = new Rect();

    private boolean shouldCacheToBitmap;

    public LineGraph(Context context) {
        this(context, null);
    }

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        ylabelPaint.setColor(Color.BLACK);
        ylabelPaint.setAlpha(180);
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
            maxY = lines.get(0).getMaxY();
            for (Line line : lines) {
                maxY = Math.max(maxY, line.getMaxY());
            }
            return maxY;
        }

    }

    public float getMinY() {
        if (isMaxYUserSet) {
            return minY;
        } else {
            float min = lines.get(0).getMinY();
            for (Line line : lines) {
                min = Math.min(min, line.getMinY());
            }
            minY = min;
            return minY;
        }
    }

    public float getMaxX() {
        float max = lines.get(0).getMaxX();
        for (Line line : lines) {
            max = Math.max(max, line.getMaxX());
        }
        maxX = max;
        return maxX;

    }

    public float getMinX() {
        float min = lines.get(0).getPoint(0).getX();
        for (Line line : lines) {
            min = Math.min(min, line.getMinX());
        }
        minX = min;
        return minX;
    }

    public void onDraw(Canvas ca) {
        if (!shouldCacheToBitmap) {
            drawToCanvas(ca);
            return;
        }
        if (fullImage == null || shouldUpdate) {
            if (fullImage != null) {
                fullImage.recycle();
            }
            fullImage = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(fullImage);

            drawToCanvas(canvas);
            Log.w("k", "caching");

            shouldUpdate = false;
        } else {
            Log.w("k", "Drawn from cache");
        }

        ca.drawBitmap(fullImage, 0, 0, null);


    }

    private void drawToCanvas(Canvas canvas) {
        if (lines == null || lines.size() == 0) return;
//        canvas.drawText("1400 messages", getWidth() / 2, getHeight() / 2, ylabelPaint);
        paint.reset();
        boolean drawLegend = lines.size() > 1;

        final float ylabelFontSpacing = ylabelPaint.getFontSpacing() + ylabelPaint.descent() + Util.dipToPixels(getContext(), 1);
        final float xaxislabelFontSpacing = xaxisLabelPaint.descent() - xaxisLabelPaint.ascent();// xaxisLabelPaint.getFontSpacing();
        final float paddingYLabelFromGrid = Util.dipToPixels(getContext(), 2);

        final float legendSize = drawLegend ? Util.dipToPixels(getContext(), 20) : 0;

        final float bottomPadding = xaxislabelFontSpacing + Util.dipToPixels(getContext(), 3);
        final float leftPadding = 0;//Util.dipToPixels(getContext(), 10);
        final float rightPadding = 0;//Util.dipToPixels(getContext(), 10);
        final float topPadding = legendSize;

        final float legendLeftPadding = Util.dipToPixels(getContext(), 10);

        final float circleRadius = Util.dipToPixels(getContext(), 5);
        final float lineWidth = Util.dipToPixels(getContext(), 2);
        final float legendLineWidth = Util.dipToPixels(getContext(), 3);
        final float legendLineLength = Util.dipToPixels(getContext(), 14);
        final float legendPaddingBetween = Util.dipToPixels(getContext(), 7);


        final float drawableHeight = getHeight() - bottomPadding - ylabelFontSpacing - legendSize;
        final float drawableWidth = getWidth() - leftPadding - rightPadding;

        final float xaxisY = getHeight() - bottomPadding;

        // Render grids
        paint.setStrokeWidth(1);
        paint.setAlpha(50);
        paint.setAntiAlias(true);


        int dataCount = lines.get(0).getPoints().size();
        int ptDeltaPerLine = dataCount / (getNumVerticalGrids() - 1); // draw a vertical line per


        for (int i = 0; i < getNumVerticalGrids(); i++) {
            float vxpos = leftPadding + (float) i * ((float) ptDeltaPerLine * (drawableWidth / (float) (dataCount - 1)));
            if (i != 0) canvas.drawLine(vxpos, xaxisY, vxpos, xaxisY - drawableHeight, paint);
            String txt = getXlabelFormatter().format(i, getNumVerticalGrids(), getMinX(), getMaxX(), ptDeltaPerLine);
            if (txt != null) {
                if (i == 0) {
                    xaxisLabelPaint.setTextAlign(Paint.Align.LEFT);
                } else if (i == getNumVerticalGrids() - 1) {
                    float width = xaxisLabelPaint.measureText(txt);
                    if (vxpos + (width / 2) > leftPadding + drawableWidth - 1) {
                        xaxisLabelPaint.setTextAlign(Paint.Align.RIGHT);
                        vxpos = leftPadding + drawableWidth - 1;
                    } else
                        xaxisLabelPaint.setTextAlign(Paint.Align.CENTER);
                } else {
                    xaxisLabelPaint.setTextAlign(Paint.Align.CENTER);
                }
                canvas.drawText(txt, vxpos, xaxisY + (xaxislabelFontSpacing), xaxisLabelPaint);
            }
        }

        for (int i = 1; i < getNumHorizontalGrids(); i++) {
            float ypos = xaxisY - ((float) (i) * drawableHeight / (float) (getNumHorizontalGrids() - 1));

            paint.setStrokeWidth(1);
            paint.setAlpha(50);
            canvas.drawLine(leftPadding, ypos, getWidth() - rightPadding, ypos, paint);
        }

        float maxY = getMaxY();
        float minY = getMinY();
        float maxX = getMaxX();
        float minX = getMinX();

        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        for (Line line : lines) {
            float oldx = 0, oldy = 0;
            int cnt = 0;
            int len = line.getPoints().size();

            paint.setColor(line.getColor());

            for (LinePoint lp : line.getPoints()) {
                float xpos = leftPadding + ((lp.getX() - minX) / (maxX - minX)) * drawableWidth;
                float ypos = xaxisY - ((lp.getY() - minY) / (maxY - minY)) * drawableHeight;
                if (cnt > 0) {
                    canvas.drawLine(oldx, oldy, xpos, ypos, paint);
                }
                if (cnt != 0 && cnt != len - 1 && line.isShowingPoints()) {
                    canvas.drawCircle(xpos, ypos, circleRadius, paint);
                }
                oldx = xpos;
                oldy = ypos;
                ++cnt;
            }
        }

        if (drawLegend) {
            float x = legendLeftPadding;
            float y = legendSize / 2f;
            paint.setStrokeWidth(legendLineWidth);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setTextSize(Util.dipToPixels(getContext(), 10));
            for (Line line : lines) {
                paint.setColor(line.getColor());
                canvas.drawLine(x, y, x + legendLineLength, y, paint);
                x += legendLineLength + legendPaddingBetween;
                paint.setColor(Color.BLACK);
                paint.setAlpha(200);
                canvas.drawText(line.getName(), x, y - (paint.descent() + paint.ascent()) / 2, paint);
                x += paint.measureText(line.getName()) + legendPaddingBetween;

            }
        }
//        to help with debugging formatting
//        {
//            Paint paint = new Paint();
//            paint.setColor(Color.RED);
//            paint.setStrokeWidth(1);
//            paint.setAntiAlias(true);
////            canvas.drawLine(0, 0, getWidth(), 0, paint);
////            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);
////            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paint);
////            canvas.drawLine(0, 0, 0, getHeight(), paint);
////            paint.setColor(Color.BLUE);
//            canvas.drawLine(0, legendSize / 2f, getWidth(), legendSize / 2f, paint);
//        }

        for (int i = 0; i < getNumHorizontalGrids(); i++) {
            float ypos = xaxisY - ((float) (i) * drawableHeight / (float) (getNumHorizontalGrids() - 1));
            String txt = getYlabelFormatter().format(i, getNumHorizontalGrids(), getMinY(), getMaxY(), ptDeltaPerLine);
            if (i == 0) {
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(2);
                paint.setAlpha(80);
                paint.setAntiAlias(true);
                canvas.drawLine(leftPadding, ypos, getWidth() - rightPadding, ypos, paint);
            }
            if (txt != null)
                canvas.drawText(txt, leftPadding, ypos - ylabelPaint.descent() - 1, ylabelPaint);
        }
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

    @Override
    public void invalidate() {
        if (fullImage != null) {
            fullImage.recycle();
            fullImage = null;
            shouldUpdate = true;
        }
        super.invalidate();
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

    public boolean isShouldCacheToBitmap() {
        return shouldCacheToBitmap;
    }

    public void setShouldCacheToBitmap(boolean shouldCacheToBitmap) {
        this.shouldCacheToBitmap = shouldCacheToBitmap;
    }

    public interface OnPointClickedListener {
        abstract void onClick(int lineIndex, int pointIndex);
    }

    public abstract interface LabelFormatter {
        abstract String format(int idx, int tot, float min, float max, int ptsPerDelta);
    }
}
