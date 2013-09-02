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

import java.util.ArrayList;

public class Line {

    private ArrayList<LinePoint> points = new ArrayList<LinePoint>();
    private int color;
    private boolean showPoints = false;
    private String name;
    private float maxX = Float.MIN_VALUE;
    private float minX = Float.MAX_VALUE;
    private float maxY = Float.MIN_VALUE;
    private float minY = Float.MAX_VALUE;


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<LinePoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LinePoint> points) {
        this.points = points;
        setMaxX(Float.MIN_VALUE);
        setMaxY(Float.MIN_VALUE);
        setMinX(Float.MAX_VALUE);
        setMinY(Float.MAX_VALUE);
        for (LinePoint pt : points) {
            setMaxX(Math.max(getMaxX(), pt.getX()));
            setMaxY(Math.max(getMaxY(), pt.getY()));
            setMinX(Math.min(getMinX(), pt.getX()));
            setMinY(Math.min(getMinY(), pt.getY()));
        }
    }

    public void addPoint(LinePoint point) {
        points.add(point);
        setMaxX(Math.max(getMaxX(), point.getX()));
        setMaxY(Math.max(getMaxY(), point.getY()));
        setMinX(Math.min(getMinX(), point.getX()));
        setMinY(Math.min(getMinY(), point.getY()));
    }

    public LinePoint getPoint(int index) {
        return points.get(index);
    }

    public int getSize() {
        return points.size();
    }

    public boolean isShowingPoints() {
        return showPoints;
    }

    public void setShowingPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }
}
