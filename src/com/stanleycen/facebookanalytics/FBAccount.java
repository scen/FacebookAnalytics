package com.stanleycen.facebookanalytics;

import android.util.Log;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FBAccount {
	public static Session session;
	public static GraphUser me;
	public static boolean initialized = false;
    public static DatabaseHandler db;
	
	static final String TAG = "FBAccount";
	
	public static void init() {
		session = Session.getActiveSession();
		
		Request.newMeRequest(session, new GraphUserCallback() {
			
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
	
	public static void logout() {
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
		initialized = false;
	}

}
