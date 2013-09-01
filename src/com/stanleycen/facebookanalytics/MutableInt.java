package com.stanleycen.facebookanalytics;

/**
 * Created by scen on 9/1/13.
 */
public class MutableInt {
    int value = 1; // note that we start at 1 since we're counting
    public void increment () { ++value;      }
    public int  get ()       { return value; }
    public void add(int val) { value += val; }
}