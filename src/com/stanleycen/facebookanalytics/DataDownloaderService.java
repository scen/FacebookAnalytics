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

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by scen on 8/29/13.
 */
public class DataDownloaderService extends Service{
    private final static String TAG = "DataDownloader";
    private static final int ONGOING_NOTIFICATION_ID = 42;

    public static final String EXTRA_MESSENGER = "com.stanleycen.facebookanalytics.DataDownloaderService.EXTRA_MESSENGER";

    enum MessageType {
        UPDATE_PROGRESSBAR,
        FINISHED_DOWNLOAD
    };

    private Messenger messenger = null;
    private boolean startedDownload = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (startedDownload) return START_NOT_STICKY;

        startedDownload = true;


        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            messenger = (Messenger)extras.get(EXTRA_MESSENGER);
        }
        else {
            Log.wtf(TAG, "extras null");
        }

        updateDownloadProgress("Retrieving conversation list", 0, 100, true);

        Thread t;

        boolean unifiedMessagingEnabled = true;
        if (unifiedMessagingEnabled) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Download thread list

                    long lastTimestamp = UnifiedMessaging.LARGE_TIMESTAMP;

                    String threadFQL = UnifiedMessaging.getThreadFQL(lastTimestamp);
                    Bundle opts = new Bundle();
                    opts.putString("q", threadFQL);

                    Request req = new Request(Session.getActiveSession(), "/fql",
                            opts, HttpMethod.GET);
                    Response res = req.executeAndWait();

                    if (res.getError() != null) {
                        Log.e(TAG, res.getError().getErrorMessage());
                        Debug.waitForDebugger();
                    }

                    JSONObject jobj = res.getGraphObject().getInnerJSONObject();
                    try {
                        JSONArray data = jobj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jcurThread = data.getJSONObject(i);
                            FBThread fbThread = new FBThread();
                            fbThread.lastUpdate = Calendar.getInstance();
                            fbThread.lastUpdate.setTimeInMillis(jcurThread.getLong("timestamp"));
                            fbThread.messageCount = jcurThread.getInt("num_messages");
                            fbThread.title = jcurThread.getString("title");
                            fbThread.id = jcurThread.getString("thread_id");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    notifyFinish();
                    stopSelf();
                }
            });
        }
        else {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    notifyFinish();
                    stopSelf();
                }
            });
        }
        t.start();
        return START_NOT_STICKY;
    }

    private void notifyFinish() {
        Message msg = Message.obtain();
        msg.what = MessageType.FINISHED_DOWNLOAD.ordinal();
        msg.obj = GlobalApp.get().fb.me.getId();
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void updateDownloadProgress(String content, int progress, int mx, boolean ongoing) {
        updateOngoingNotification(content, progress, mx, ongoing);

        Message msg = Message.obtain();
        msg.what = MessageType.UPDATE_PROGRESSBAR.ordinal();
        ProgressBarUpdate progressBarUpdate = new ProgressBarUpdate();
        progressBarUpdate.content = content;
        progressBarUpdate.progress = progress;
        progressBarUpdate.mx = mx;
        progressBarUpdate.ongoing = ongoing;
        msg.obj = progressBarUpdate;

        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    public class ProgressBarUpdate {
        String content;
        int progress;
        int mx;
        boolean ongoing;
    }
}
