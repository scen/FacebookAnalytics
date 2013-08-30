package com.stanleycen.facebookanalytics;

import android.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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
import android.util.MalformedJsonException;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by scen on 8/29/13.
 */
public class DataDownloaderService extends Service {
    private final static String TAG = "DataDownloader";
    private static final int ONGOING_NOTIFICATION_ID = 42;

    public static final String EXTRA_MESSENGER = "com.stanleycen.facebookanalytics.DataDownloaderService.EXTRA_MESSENGER";

    enum MessageType {
        UPDATE_PROGRESSBAR,
        FINISHED_DOWNLOAD
    }

    ;

    private Messenger messenger = null;
    private boolean startedDownload = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (startedDownload) return START_NOT_STICKY;

        startedDownload = true;


        Bundle extras = intent.getExtras();
        if (extras != null) {
            messenger = (Messenger) extras.get(EXTRA_MESSENGER);
        } else {
            Log.wtf(TAG, "extras null");
        }

        updateDownloadProgress("Retrieving conversation list", 0, 100, true);

        Thread t;

        if (GlobalApp.get().fb.fbData.collectionMethod == FBData.CollectionMethod.UNIFIED_API) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Download thread list

                    FBData newFbData = new FBData();

                    long lastTimestamp = UnifiedMessaging.LARGE_TIMESTAMP;

                    int totMessageCount = 0;
                    int howMany = 1;
                    outer:
                    while (true) {
                        String threadFQL = UnifiedMessaging.getThreadFQL(lastTimestamp);
                        Bundle opts = new Bundle();
                        opts.putString("q", threadFQL);

                        Request req = new Request(Session.getActiveSession(), "/fql",
                                opts, HttpMethod.GET);
                        Response res = req.executeAndWait();

                        if (res.getError() != null) {
                            if (handleFBResponseError(res.getError())) continue;
                        }

                        JSONObject jobj = res.getGraphObject().getInnerJSONObject();
                        try {
                            JSONArray data = jobj.getJSONArray("data");
                            if (data == null || data.length() == 0) break;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jcurThread = data.getJSONObject(i);
                                FBThread fbThread = new FBThread();
                                long timestamp = jcurThread.getLong("timestamp");
                                fbThread.lastUpdate = new DateTime(timestamp);
                                fbThread.messageCount = jcurThread.getInt("num_messages");
                                totMessageCount += fbThread.messageCount;
                                fbThread.title = jcurThread.getString("title");
                                Log.d(TAG, fbThread.title);
                                fbThread.id = jcurThread.getString("thread_id");
                                fbThread.isGroupConversation = jcurThread.getBoolean("is_group_conversation");

                                JSONArray participants = jcurThread.getJSONArray("participants");
                                for (int j = 0; j < participants.length(); j++) {
                                    JSONObject curp = participants.getJSONObject(j);
                                    String uid = curp.getString("user_id");
                                    FBUser user = new FBUser(uid, curp.getString("name"));
                                    fbThread.participants.add(uid);
                                    GlobalApp.get().fb.fbData.userMap.put(uid, user);
                                }

                                JSONArray formerParticipants = jcurThread.getJSONArray("former_participants");
                                for (int j = 0; j < formerParticipants.length(); j++) {
                                    JSONObject curp = formerParticipants.getJSONObject(j);
                                    String uid = curp.getString("user_id");
                                    FBUser user = new FBUser(uid, curp.getString("name"));
                                    fbThread.participants.add(uid);
                                    GlobalApp.get().fb.fbData.userMap.put(uid, user);
                                }
                                GlobalApp.get().fb.fbData.threads.add(fbThread);
                                lastTimestamp = timestamp;
                                if (--howMany <= 0) break outer;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SystemClock.sleep(UnifiedMessaging.API_WAIT);
                    }


                    int curIdx = 1;
                    int tot = GlobalApp.get().fb.fbData.threads.size();
                    int messagesDownloaded = 0;

                    for (FBThread fbThread : GlobalApp.get().fb.fbData.threads) {
                        int curThreadMessagesDownloaded = 0;
                        lastTimestamp = 0;
                        try {
                            while (true) {
                                updateDownloadProgress("Conversation " + curIdx + " of " + tot + " (" + curThreadMessagesDownloaded + " / " + fbThread.messageCount + ")",
                                        messagesDownloaded, totMessageCount, false);
                                String messagesFQL = UnifiedMessaging.getMessagesFQL(fbThread.id, lastTimestamp);

                                Bundle opts = new Bundle();
                                opts.putString("q", messagesFQL);

                                Request req = new Request(Session.getActiveSession(), "/fql",
                                        opts, HttpMethod.GET);
                                Response res = req.executeAndWait();

                                if (res.getError() != null) {
                                    if (handleFBResponseError(res.getError())) continue;
                                }

                                JSONObject jobj = res.getGraphObject().getInnerJSONObject();
                                JSONArray data = jobj.getJSONArray("data");
                                if (data == null || data.length() == 0) break;
                                Log.v(TAG, "" + data.length());
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject curMessage = data.getJSONObject(i);
                                    FBMessage fbMessage = new FBMessage();
                                    long ts = curMessage.getLong("timestamp");
                                    fbMessage.timestamp = new DateTime(ts);
                                    lastTimestamp = ts;
                                    if (!curMessage.isNull("coordinates")) {
                                        JSONObject coordinates = curMessage.optJSONObject("coordinates");
                                        if (coordinates != null) {
                                            fbMessage.hasCoordinates = true;
                                            fbMessage.latitude = (float)coordinates.getDouble("latitude");
                                            fbMessage.longitude = (float)coordinates.getDouble("longitude");
                                        }
                                    }
                                    fbMessage.body = curMessage.getString("body");
                                    JSONObject curp = curMessage.optJSONObject("sender");
                                    if (curp != null) {
                                        String uid = curp.getString("user_id");
                                        FBUser user = new FBUser(uid, curp.getString("name"));
                                        fbMessage.from = uid;
                                        GlobalApp.get().fb.fbData.userMap.put(uid, user);
                                    }
                                    fbMessage.id = curMessage.getString("message_id");
                                    JSONArray attachments = curMessage.optJSONArray("attachments");
                                    if (attachments != null && attachments.length() > 0) {
                                        JSONObject attachmentMap = curMessage.getJSONObject("attachment_map");
                                        if (attachmentMap != null) {
                                            for (int j = 0; j < attachments.length(); j++) {
                                                String id = attachments.getString(j);
                                                JSONObject attachmentObj = attachmentMap.getJSONObject(id);
                                                if (attachmentObj != null) {
                                                    JSONObject imageData = attachmentObj.getJSONObject("image_data");
                                                    if (imageData != null) {
                                                        FBAttachment fbAttachment = new FBAttachment();
                                                        fbAttachment.height = imageData.getInt("height");
                                                        fbAttachment.width = imageData.getInt("width");
                                                        fbAttachment.mimeType = attachmentObj.getString("mime_type");
                                                        fbAttachment.url = imageData.getString("url");
                                                        fbAttachment.previewUrl = imageData.getString("preview_url");
                                                        fbAttachment.id = id;
                                                        fbAttachment.type = FBAttachment.Type.IMAGE;
                                                        fbAttachment.message = fbMessage.id;
                                                        fbAttachment.thread = fbThread.id;
                                                        fbMessage.attachments.add(fbAttachment);
                                                        Log.d(TAG, fbAttachment.url);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    fbMessage.source = FBMessage.Source.OTHER;
                                    JSONArray tags = curMessage.optJSONArray("tags");
                                    if (tags != null && tags.length() > 0) {
                                        for (int k = 0; k < tags.length(); k++) {
                                            String tag = tags.getString(k);
                                            if (tag.startsWith("source:mobile")) {
                                                fbMessage.source = FBMessage.Source.MOBILE;
                                            }
                                            else if (tag.startsWith("source:chat")) {
                                                fbMessage.source = FBMessage.Source.WEB;
                                            }
                                        }
                                    }
                                    JSONArray shares = curMessage.optJSONArray("shares");
                                    if (shares != null && shares.length() > 0) {
                                        if (!curMessage.isNull("share_map")) {
//                                            Log.d(TAG, curMessage.toString(2));
                                            JSONObject shareMap = curMessage.optJSONObject("share_map");
                                            if (shareMap != null) {
                                                for (int k = 0; k < shares.length(); k++) {
                                                    String id = shares.getString(k);
                                                    JSONObject shareObj = shareMap.getJSONObject(id);
                                                    if (shareObj != null) {
                                                        if (!shareObj.isNull("sticker_id")) {
                                                            FBAttachment sticker = new FBAttachment();
                                                            sticker.type = FBAttachment.Type.STICKER;
                                                            sticker.url = shareObj.getString("href");
                                                            sticker.message = fbMessage.id;
                                                            sticker.thread = fbThread.id;
                                                            Log.v(TAG, sticker.url);
                                                            fbMessage.attachments.add(sticker);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    fbMessage.thread = fbThread.id;
                                    fbThread.messages.add(fbMessage);
                                    ++messagesDownloaded;
                                    ++curThreadMessagesDownloaded;
                                }
                                SystemClock.sleep(UnifiedMessaging.API_WAIT);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fbThread.messageCount = fbThread.messages.size();
                        ++curIdx;
                    }

                    GlobalApp.get().fb.fbData.lastUpdate = DateTime.now();

                    updateDownloadProgress("Committing data", 100, 100, true);
                    UnifiedMessaging.commitData(GlobalApp.get().fb.fbData);

                    notifyFinish();
                    stopSelf();
                }
            });
        } else {
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

    private boolean handleFBResponseError(FacebookRequestError error) {
        Log.e(TAG, error.getErrorMessage());
        int code = error.getErrorCode();
        switch (code) {
            case 1:
            case 2:
            case 4:
            case 17:
                SystemClock.sleep(UnifiedMessaging.API_TIMEOUT_WAIT);
                return true;
            default:
                Debug.waitForDebugger();
        }
        return false;
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
