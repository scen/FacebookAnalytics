package com.stanleycen.facebookanalytics;

import android.util.Log;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FBAccount {
	public Session session;
	public GraphUser me;
	public boolean initialized = false;
    public FBData fbData;

	static final String TAG = "FBAccount";
	
	public void init() {
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
        initData();
		initialized = true;
	}

    public void initData() {
        fbData = new FBData();
        fbData.collectionMethod = FBData.CollectionMethod.UNIFIED_API;
    }

	public void logout() {
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
		initialized = false;
	}

}
