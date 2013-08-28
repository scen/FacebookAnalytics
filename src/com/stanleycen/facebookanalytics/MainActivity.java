package com.stanleycen.facebookanalytics;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.ProfilePictureView;


public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	final static int PREFERENCESACTIVITY_CODE = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 
        		R.string.drawer_open, R.string.drawer_close) {
        	public void onDrawerClosed(View view) {
        		invalidateOptionsMenu();
        	}
        	
        	public void onDrawerOpened(View drawerView) {
        		invalidateOptionsMenu();
        	}
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        new GetMeTask().execute();
    }
    
    private class DrawerRowAdapter extends ArrayAdapter<String> {
    	private ArrayList<String> arr = new ArrayList<String>();
    	private int[] res;
    	Context context;
    	
    	public DrawerRowAdapter(Context context, ArrayList<String> arr, int[] res) {
    		super(context, R.layout.drawer_list_item, arr);
    		this.context = context;
    		this.arr = arr;
    		this.res = res;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View rowView = convertView;
    		
    		RowHolder holder = new RowHolder();
    		if (convertView == null) {
    			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    		
    		holder.text.setText(arr.get(position));
    		holder.icon.setImageResource(res[position]);
			return rowView;
    	}
    };
    
    private void initDrawer() {
    	LayoutInflater inflater = getLayoutInflater();
        View top = (View)inflater.inflate(R.layout.profile, mDrawerList, false);
        ProfilePictureView myProfilePic = (ProfilePictureView)top.findViewById(R.id.profilepic);
        myProfilePic.setProfileId(FBAccount.me.getId());
        myProfilePic.setPresetSize(ProfilePictureView.SMALL);
        TextView userName = (TextView)top.findViewById(R.id.username);
        userName.setText(FBAccount.me.getName());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.addHeaderView(top, null, false);
        
        final String[] navText = {"Data collection", "Overview", "Conversations", "Group chats"};
        final int[] navIcons   = {R.drawable.ic_action_data, R.drawable.ic_action_overview, R.drawable.ic_social_person, R.drawable.ic_social_group};
        mDrawerList.setAdapter(new DrawerRowAdapter(this, new ArrayList<String>(Arrays.asList(navText)), 
        		navIcons));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener() {
		});
    }
    
    private static class RowHolder {
		public TextView text;
		public ImageView icon;
	}
    
    private class GetMeTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Retrieving account info");
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			FBAccount.init();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			initDrawer();
			
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
    	FBAccount.logout();
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
    
    private void drawerSelect(int position) {
    	mDrawerList.setItemChecked(position, true);
    	
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
    
}
