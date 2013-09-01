package com.stanleycen.facebookanalytics;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by scen on 8/29/13.
 */
public class FBData {
    public DateTime lastUpdate = null;
    public CollectionMethod collectionMethod = CollectionMethod.UNIFIED_API;

    public ArrayList<FBThread> threads = new ArrayList<FBThread>();
    public HashMap<String, FBUser> userMap = new HashMap<String, FBUser>();

    public void computeHighLevelThreadStats() {
        me = userMap.get(GlobalApp.get().fb.me.getId());
        for (FBThread fbThread : threads) {
            fbThread.charCount = 0;
            for (FBMessage fbMessage : fbThread.messages) {
                fbThread.charCount += fbMessage.body.length();
            }
            if (!fbThread.isGroupConversation) {
                for (FBUser person : fbThread.participants) {
                    if (!person.id.equals(GlobalApp.get().fb.me.getId())) {
                        fbThread.other = person;
                        break;
                    }
                }
            }
        }
    }

    FBUser me;

    public enum CollectionMethod {
        OLD_API,
        UNIFIED_API
    }
}
