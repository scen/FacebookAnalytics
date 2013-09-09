package com.stanleycen.facebookanalytics;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.Session;
import com.facebook.SessionState;

import java.util.Arrays;


public class LoginActivity extends Activity implements OnClickListener {
    private Session.StatusCallback mStatusCallback = new SessionStatusCallback();
    private FBLoginButton loginButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (FBLoginButton) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else if (session == null) {
            SharedPreferences prefs = getSharedPreferences(GlobalApp.PREFS_FILE, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("FBID");
            editor.remove("FBNAME");
            editor.commit();
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, mStatusCallback, savedInstanceState);
            }
            if (session == null) {
                String appId = getString(R.string.app_id);
                session = new Session.Builder(getBaseContext()).setApplicationId(appId).build();
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(mStatusCallback));
            }
        }
    }

    @Override
    public void onClick(View view) {
        Session session = Session.getActiveSession();
        Log.v("fb", "onClick");
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList("read_mailbox", "basic_info")).setCallback(mStatusCallback));
        } else {
            Session.openActiveSession(this, true, mStatusCallback);
        }
    }

    private void sessionStatusChanged() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                .onActivityResult(this, requestCode, resultCode, data);
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            Log.v("fb", state.toString());
            sessionStatusChanged();
        }
    }
}
