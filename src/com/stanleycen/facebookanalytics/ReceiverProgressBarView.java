package com.stanleycen.facebookanalytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * Created by scen on 8/31/13.
 */
public class ReceiverProgressBarView extends ProgressBar {
    ProgressBroadcastReceiver receiver;


    public ReceiverProgressBarView(Context context) {
        super(context);
        init();
    }

    private void init() {
        receiver = new ProgressBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.stanleycen.facebookanalytics.progress");
        filter.addCategory("android.intent.category.DEFAULT");
        getContext().registerReceiver(receiver, filter);
    }

    public ReceiverProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReceiverProgressBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDetachedFromWindow() {
        getContext().unregisterReceiver(receiver);
        super.onDetachedFromWindow();
    }

    public class ProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle e = intent.getExtras();
            if (e != null) {
                String s = e.getString("action", "");
                if (s.equals("indeterminate")) {
                    setIndeterminate(true);
                }
                else if (s.equals("update")) {
                    setIndeterminate(false);
                    setProgress(e.getInt("progress", 0));
                }
            }
        }
    }
}
