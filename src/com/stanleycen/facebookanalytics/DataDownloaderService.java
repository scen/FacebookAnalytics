package com.stanleycen.facebookanalytics;

import android.*;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by scen on 8/29/13.
 */
public class DataDownloaderService extends Service{
    private final static String TAG = "DataDownloader";
    private static final int ONGOING_NOTIFICATION_ID = 42;
    public static final String EXTRA_MESSENGER = "com.stanleycen.facebookanalytics.DataDownloaderService.EXTRA_MESSENGER";

    private Messenger messenger = null;
    private boolean startedDownload;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            messenger = (Messenger)extras.get(EXTRA_MESSENGER);
        }
        else {
            Log.wtf(TAG, "extras null");
        }

        updateOngoingNotification("Download in progress", 0, 100, true);

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i <= 100; i += 10) {
                    updateOngoingNotification("Download in progress", i, 100, false);
                    SystemClock.sleep(1000);
                }
                Message msg = Message.obtain();
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        });
        t.start();
        return START_NOT_STICKY;
    }

    private void updateOngoingNotification(String content, int progress, int mx, boolean ongoing) {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setContentTitle("Facebook message download")
                .setContentText(content)
                .setTicker("Starting Facebook message download")
                .setSmallIcon(R.drawable.stat_sys_download)
                .setProgress(mx, progress, ongoing)
                .setContentIntent(pi);
        Notification n = b.build();

        startForeground(ONGOING_NOTIFICATION_ID, n);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
