package com.stanleycen.facebookanalytics;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.HashMap;


/**
 * Created by scen on 8/29/13.
 */
public class UnifiedMessaging {
    public final static long LARGE_TIMESTAMP = 10000000000000000L;
    public final static int API_WAIT = 1500;
    public final static int API_TIMEOUT_WAIT = 1000 * 60 * 5;

    private final static String GET_THREADS_FQL =
            "SELECT former_participants,is_group_conversation,title,num_messages,participants,thread_id,timestamp FROM unified_thread WHERE folder=\"inbox\" AND timestamp < %d LIMIT 500";
    private final static String GET_MESSAGES_FQL =
            "SELECT attachment_map,attachments,body,coordinates,message_id,sender,timestamp,shares,share_map,tags FROM unified_message WHERE thread_id=\"%s\" AND timestamp > %d LIMIT 500";

    public static String getThreadFQL(long timestamp) {
        return String.format(GET_THREADS_FQL, timestamp);
    }

    public static String getMessagesFQL(String threadId, long timestamp) {
        return String.format(GET_MESSAGES_FQL, threadId, timestamp);
    }

    public static FBData readAllFromDatabase() {
        FBData fbData = new FBData();

        DatabaseHandler dbHelper = GlobalApp.get().db;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String lastupdate = dbHelper.getKeyVal("lastupdate");
        String collectionMethod = dbHelper.getKeyVal("collectionmethod");

        if (lastupdate != null && collectionMethod != null) {
            fbData.lastUpdate = new DateTime(Long.parseLong(lastupdate));
            fbData.collectionMethod = FBData.CollectionMethod.values()[Integer.parseInt(collectionMethod)];

            Cursor usersCursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_USERS, null);
            usersCursor.moveToFirst();
            while (!usersCursor.isAfterLast()) {
                String id = usersCursor.getString(0);
                String name = usersCursor.getString(1);
                fbData.userMap.put(id, new FBUser(id, name));
                usersCursor.moveToNext();
            }

            HashMap<String, FBThread> threadIDMap = new HashMap<String, FBThread>();
            HashMap<String, FBMessage> threadAndMessageIDMap = new HashMap<String, FBMessage>();

            Cursor threadCursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_THREADS + " ORDER BY " + DatabaseHandler.COLUMN_TIMESTAMP + " DESC", null);
            threadCursor.moveToFirst();
            while (!threadCursor.isAfterLast()) {
                FBThread fbThread = new FBThread();
                fbThread.id = threadCursor.getString(0);
                threadIDMap.put(fbThread.id, fbThread);
                fbThread.title = threadCursor.getString(1);
                fbThread.lastUpdate = new DateTime(threadCursor.getLong(2));
                fbThread.isGroupConversation = threadCursor.getInt(3) == 1;

                try {
                    JSONArray jsonParticipants = new JSONArray(threadCursor.getString(4));
                    if (jsonParticipants != null) {
                        for (int i = 0; i < jsonParticipants.length(); i++) {
                            fbThread.participants.add(fbData.userMap.get(jsonParticipants.getString(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fbThread.messageCount = threadCursor.getInt(5);
                fbData.threads.add(fbThread);
                threadCursor.moveToNext();
            }

            Cursor msgCursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_MESSAGES + " ORDER BY timestamp", null);
            msgCursor.moveToFirst();
            while (!msgCursor.isAfterLast()) {
                FBMessage fbMessage = new FBMessage();
                fbMessage.id = msgCursor.getString(0);
                fbMessage.from = msgCursor.getString(1);
                fbMessage.timestamp = new DateTime(msgCursor.getLong(2));
                fbMessage.body = msgCursor.getString(3);
                fbMessage.thread = msgCursor.getString(4);
                fbMessage.hasCoordinates = msgCursor.getInt(5) == 1;
                if (fbMessage.hasCoordinates) {
                    fbMessage.latitude = msgCursor.getFloat(6);
                    fbMessage.longitude = msgCursor.getFloat(7);
                }
                fbMessage.source = FBMessage.Source.values()[msgCursor.getInt(9)];
                threadAndMessageIDMap.put(fbMessage.thread + fbMessage.id, fbMessage);
                FBThread parentThread = threadIDMap.get(fbMessage.thread);
                parentThread.messages.add(fbMessage);
                msgCursor.moveToNext();
            }
            Cursor aCursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_ATTACHMENTS, null);
            aCursor.moveToFirst();
            while (!aCursor.isAfterLast()) {
                FBAttachment fbAttachment = new FBAttachment();
                fbAttachment.id = aCursor.getString(0);
                fbAttachment.type = FBAttachment.Type.values()[aCursor.getInt(6)];
                fbAttachment.url = aCursor.getString(3);
                fbAttachment.thread = aCursor.getString(8);
                fbAttachment.message = aCursor.getString(7);
                if (fbAttachment.type == FBAttachment.Type.IMAGE) {
                    fbAttachment.width = aCursor.getInt(1);
                    fbAttachment.height = aCursor.getInt(2);
                    fbAttachment.previewUrl = aCursor.getString(4);
                    fbAttachment.mimeType = aCursor.getString(5);
                }
                String combinedId = fbAttachment.thread + fbAttachment.message;
                threadAndMessageIDMap.get(combinedId).attachments.add(fbAttachment);
                aCursor.moveToNext();
            }
        }

        db.close();

        Log.w("k", "READ");

        fbData.computeHighLevelThreadStats();

        return fbData;
    }

    public static void commitData(FBData fbData) {
        DatabaseHandler dbHelper = GlobalApp.get().db;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try
        {
            dbHelper.clearAllTables(db);

            dbHelper.insertKeyVal(db, "lastupdate", String.valueOf(fbData.lastUpdate.getMillis()));
            dbHelper.insertKeyVal(db, "collectionmethod", String.valueOf(fbData.collectionMethod.ordinal()));

            for (HashMap.Entry<String, FBUser> kv : fbData.userMap.entrySet()) {
                FBUser user = kv.getValue();
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHandler.COLUMN_ID, user.id);
                cv.put(DatabaseHandler.COLUMN_NAME, user.name);
                db.insert(DatabaseHandler.TABLE_USERS, null, cv);
            }

            for (FBThread fbThread : fbData.threads) {
                commitThread(db, fbThread);
            }
            db.setTransactionSuccessful();
            GlobalApp.get().fb.fbData = fbData;
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public static void commitThread(SQLiteDatabase db, FBThread fbThread) {
        db.delete(DatabaseHandler.TABLE_THREADS, DatabaseHandler.COLUMN_ID + "=?", new String[] { fbThread.id });
        db.delete(DatabaseHandler.TABLE_MESSAGES, DatabaseHandler.COLUMN_THREAD + "=?", new String[] { fbThread.id });
        db.delete(DatabaseHandler.TABLE_ATTACHMENTS, DatabaseHandler.COLUMN_THREAD + "=?", new String[] { fbThread.id });

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.COLUMN_ID, fbThread.id);
        cv.put(DatabaseHandler.COLUMN_TITLE, fbThread.title);
        cv.put(DatabaseHandler.COLUMN_TIMESTAMP, fbThread.lastUpdate.getMillis());
        cv.put(DatabaseHandler.COLUMN_ISGROUP, fbThread.isGroupConversation);

        JSONArray participantsArray = new JSONArray();
        for (FBUser user : fbThread.participants) {
            participantsArray.put(user.id);
        }

        cv.put(DatabaseHandler.COLUMN_PARTICIPANTS, participantsArray.toString());
        cv.put(DatabaseHandler.COLUMN_MESSAGECOUNT, fbThread.messageCount);

        db.insert(DatabaseHandler.TABLE_THREADS, null, cv);

        for (FBMessage fbMessage : fbThread.messages) {
            commitMessage(db, fbMessage);
        }
    }

    public static void commitMessage(SQLiteDatabase db, FBMessage fbMessage) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHandler.COLUMN_ID, fbMessage.id);
        cv.put(DatabaseHandler.COLUMN_FROM, fbMessage.from);
        cv.put(DatabaseHandler.COLUMN_TIMESTAMP, fbMessage.timestamp.getMillis());
        cv.put(DatabaseHandler.COLUMN_BODY, fbMessage.body);
        cv.put(DatabaseHandler.COLUMN_THREAD, fbMessage.thread);
        cv.put(DatabaseHandler.COLUMN_HASCOORD, fbMessage.hasCoordinates);
        if (fbMessage.hasCoordinates) {
            cv.put(DatabaseHandler.COLUMN_LATITUDE, fbMessage.latitude);
            cv.put(DatabaseHandler.COLUMN_LONGITUDE, fbMessage.longitude);
        }
        if (!fbMessage.attachments.isEmpty()) {
            JSONArray attachmentArray = new JSONArray();
            for (FBAttachment fbAttachment : fbMessage.attachments) {
                attachmentArray.put(fbAttachment.id);
                commitAttachment(db, fbAttachment);
            }
            cv.put(DatabaseHandler.COLUMN_ATTACHMENTS, attachmentArray.toString());
        }
        cv.put(DatabaseHandler.COLUMN_SOURCE, fbMessage.source.ordinal());

        db.insert(DatabaseHandler.TABLE_MESSAGES, null, cv);
    }

    public static void commitAttachment(SQLiteDatabase db, FBAttachment fbAttachment) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHandler.COLUMN_ID, fbAttachment.id);
        cv.put(DatabaseHandler.COLUMN_TYPE, fbAttachment.type.ordinal());
        cv.put(DatabaseHandler.COLUMN_URL, fbAttachment.url);
        cv.put(DatabaseHandler.COLUMN_THREAD, fbAttachment.thread);
        cv.put(DatabaseHandler.COLUMN_MESSAGE, fbAttachment.message);
        if (fbAttachment.type == FBAttachment.Type.IMAGE) {
            cv.put(DatabaseHandler.COLUMN_PREVIEWURL, fbAttachment.previewUrl);
            cv.put(DatabaseHandler.COLUMN_MIMETYPE, fbAttachment.mimeType);
            cv.put(DatabaseHandler.COLUMN_HEIGHT, fbAttachment.height);
            cv.put(DatabaseHandler.COLUMN_WIDTH, fbAttachment.width);
        }

        db.insert(DatabaseHandler.TABLE_ATTACHMENTS, null, cv);
    }
}
