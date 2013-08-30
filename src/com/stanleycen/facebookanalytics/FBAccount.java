package com.stanleycen.facebookanalytics;

import android.util.Log;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FBAccount {
	public GraphUser me;
	public boolean initialized = false;
    public FBData fbData;

	static final String TAG = "FBAccount";

    public Session session() {
        Session ret =  Session.getActiveSession();
        if (ret == null || !ret.isOpened()) {
            Log.wtf("FBAccount", "session null or not open");
        }
        return ret;
    }

	public void init() {

		Request.newMeRequest(session(), new GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (response.getError() != null)
				{
					Log.e(TAG, response.getError().toString());
				}
				me = user;
				Log.d(TAG, me.getId());
			}
		}).executeAndWait();

		initialized = true;
	}

	public void logout() {
		if (!session().isClosed()) {
			session().closeAndClearTokenInformation();
		}
		initialized = false;
	}

}
