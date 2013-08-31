package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.TimeZone;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class MainActivity extends Activity {
    public static final int DRAWER_DATA_COLLECT = 1;

    private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<DrawerEntry> mEntries;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private final String TAG = "MAIN";
	
	final static int PREFERENCESACTIVITY_CODE = 1;

    public class DrawerEntry {
        String text;
        int icon;
        String fragment;

        public DrawerEntry(String text, int icon, String fragment) {
            this.text = text;
            this.icon = icon;
            this.fragment = fragment;
        }
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);

        mTitle = mDrawerTitle = getString(R.string.app_name);
//        Log.d(TAG, (String) mDrawerTitle);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 
        		R.string.drawer_open, R.string.drawer_close) {
        	public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
        		invalidateOptionsMenu();
        	}
        	
        	public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
        		invalidateOptionsMenu();
        	}
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
        new InitializeTask().execute();
    }

    private ArrayList<String> getStringsFromEntries(ArrayList<DrawerEntry> entries) {
        ArrayList<String> arr = new ArrayList<String>();
        for (DrawerEntry entry : entries) {
            arr.add(entry.text);
        }
        return arr;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private class DrawerRowAdapter extends ArrayAdapter<String> {
    	private ArrayList<DrawerEntry> entries = new ArrayList<DrawerEntry>();
    	Context context;



    	public DrawerRowAdapter(Context context, ArrayList<DrawerEntry> entries) {
    		super(context, R.layout.drawer_list_item, getStringsFromEntries(entries));
    		this.context = context;
            this.entries = entries;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View rowView = convertView;
    		
    		RowHolder holder = new RowHolder();
    		if (convertView == null) {
    			LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
    			rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);
    			TextView text = (TextView) rowView.findViewById(R.id.text);
    			ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
    			
    			holder.icon = icon;
    			holder.text = text;

    			rowView.setTag(holder);
    		}
    		else {
    			holder = (RowHolder)rowView.getTag();
    		}

    		holder.text.setText(entries.get(position).text);
    		holder.icon.setImageResource(entries.get(position).icon);
			return rowView;
    	}
    };
    
    private void initDrawer() {
    	LayoutInflater inflater = getLayoutInflater();
        View top = (View)inflater.inflate(R.layout.profile, mDrawerList, false);
        ProfilePictureView myProfilePic = (ProfilePictureView)top.findViewById(R.id.profilepic);
        GlobalApp app = (GlobalApp)getApplication();
        myProfilePic.setProfileId(app.fb.me.getId());
        myProfilePic.setPresetSize(ProfilePictureView.SMALL);
        TextView userName = (TextView)top.findViewById(R.id.username);
        userName.setText(app.fb.me.getName());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.addHeaderView(top, null, false);

        mEntries = new ArrayList<DrawerEntry>();
        mEntries.add(new DrawerEntry("Data collection", R.drawable.ic_action_data, DataFragment.class.getCanonicalName()));
        mEntries.add(new DrawerEntry("Overview", R.drawable.ic_action_overview, OverviewFragment.class.getCanonicalName()));
        mEntries.add(new DrawerEntry("Conversations", R.drawable.ic_social_person, ConversationsFragment.class.getCanonicalName()));
        mEntries.add(new DrawerEntry("Group chats", R.drawable.ic_social_group, ""));

        mDrawerList.setAdapter(new DrawerRowAdapter(this, mEntries));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }
    
    private static class RowHolder {
		public TextView text;
		public ImageView icon;
	}
    
    private class InitializeTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Initializing");
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
            GlobalApp app = (GlobalApp)getApplication();
            if (app.fb == null) app.fb = new FBAccount();
			app.fb.init();
			if (app.db == null) app.db = new DatabaseHandler(MainActivity.this);

            // random code to initialize timezones
            DateTime dt = DateTime.now();
            DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getDefault());
            dt.dayOfMonth();
            Log.v(TAG, Util.getTimeWithTZ(dt));

            if (app.fb.fbData != null && app.fb.fbData.lastUpdate != null) {

            }
            else {
                app.fb.fbData = UnifiedMessaging.readAllFromDatabase();
            }

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			initDrawer();
            drawerSelect(DRAWER_DATA_COLLECT);
			super.onPostExecute(result);
		}
		
	};
    
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void logoutFacebook() {
        GlobalApp app = (GlobalApp)getApplication();
    	app.fb.logout();
		finish();
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        switch(item.getItemId()) {
        case R.id.action_settings:
//        	Toast.makeText(this,  "Settings", Toast.LENGTH_SHORT).show();
        	Intent i = new Intent(this, PreferencesActivity.class);
        	startActivityForResult(i, PREFERENCESACTIVITY_CODE);
        	break;
        case R.id.action_help:
        	break;
        case R.id.action_logout:
        	logoutFacebook();
        	break;
        case R.id.action_about:
        	break;
    	default:
    		break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	case PREFERENCESACTIVITY_CODE:
    		if (resultCode == PreferencesActivity.FACEBOOK_LOGOUT) {
    			logoutFacebook();
    		}
    		break;
    	}
    }
    
    public void drawerSelect(int position) {
    	mDrawerList.setItemChecked(position, true);
        --position; // ignore header
        if (!mEntries.get(position).fragment.isEmpty()) {
            Fragment f = Fragment.instantiate(this, mEntries.get(position).fragment);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
            setTitle(mEntries.get(position).text);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public void reloadPosition(int position) {
        mDrawerList.setItemChecked(position, true);
        --position; // ignore header
        if (!mEntries.get(position).fragment.isEmpty()) {
            Fragment f = Fragment.instantiate(this, mEntries.get(position).fragment);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commitAllowingStateLoss();
            setTitle(mEntries.get(position).text);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	@Override
    	public void onItemClick(AdapterView parent, View view, int position, long id) {
    		drawerSelect(position);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }


    
}
