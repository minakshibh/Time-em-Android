package com.time_em.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.authentication.LoginActivity;
import com.time_em.barcode.CameraOpenActivity;
import com.time_em.barcode.NFCReadActivity;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.Notification;
import com.time_em.model.SyncData;
import com.time_em.model.TaskEntry;
import com.time_em.notifications.NotificationListActivity;
import com.time_em.notifications.SendNotification;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.profile.MyProfileActivity;
import com.time_em.tasks.TaskListActivity;
import com.time_em.team.UserListActivity;
import com.time_em.utils.FileUtils;
import com.time_em.utils.Utils;

import junit.runner.TestSuiteLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class BaseActivity extends Activity implements  AsyncResponseTimeEm{

	public ActionBarDrawerToggle mDrawerToggle;
	public DrawerLayout mDrawerLayout;
	public RelativeLayout flyoutDrawerRl;
	
	public AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
	public RelativeLayout contentFrame, profile, sync, scanBarcode, nfcTapping, logout;
	public RelativeLayout slider;
	public LinearLayout myTasks, myTeam, lay_notifications,settings;
	private Resources resources;
	public ImageView menuUserStatus,imageSync;

	private TimeEmDbHandler dbHandler;
	private Time_emJsonParser parser;
	private FileUtils fileUtils;




	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);
	
		initScreen();
		setClickListeners();
		setListenerOnDrawer();
	}
	
	private void initScreen(){
		parser = new Time_emJsonParser(BaseActivity.this);
		fileUtils = new FileUtils(BaseActivity.this);
		slider = (RelativeLayout)findViewById(R.id.btnSlider);
		contentFrame = (RelativeLayout)findViewById(R.id.content_frame);
		profile = (RelativeLayout)findViewById(R.id.profile);
		sync = (RelativeLayout)findViewById(R.id.sync);
		scanBarcode = (RelativeLayout)findViewById(R.id.scanBarcode);
		nfcTapping = (RelativeLayout)findViewById(R.id.nfcTapping);
		logout = (RelativeLayout)findViewById(R.id.signOut);
		imageSync=(ImageView)findViewById(R.id.imageSync);
		flyoutDrawerRl=(RelativeLayout)findViewById(R.id.left_drawer);
		
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		
		myTasks = (LinearLayout) findViewById(R.id.myTasks);
		myTeam = (LinearLayout) findViewById(R.id.myTeam);
		lay_notifications = (LinearLayout) findViewById(R.id.notifications);
		settings = (LinearLayout) findViewById(R.id.settings);
		resources = BaseActivity.this.getResources();
		menuUserStatus = (ImageView) findViewById(R.id.menuUserStatus);
	}
	
	private void setClickListeners(){
		slider.setOnClickListener(drawerListener);
		profile.setOnClickListener(drawerListener);
		sync.setOnClickListener(drawerListener);
		scanBarcode.setOnClickListener(drawerListener);
		nfcTapping.setOnClickListener(drawerListener);
		logout.setOnClickListener(drawerListener);
		myTasks.setOnClickListener(drawerListener);
		myTeam.setOnClickListener(drawerListener);
		lay_notifications.setOnClickListener(drawerListener);
		settings.setOnClickListener(drawerListener);
	}
	
	public View.OnClickListener drawerListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == profile){
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
				}
				Intent mIntent = new Intent(BaseActivity.this, MyProfileActivity.class);
				startActivity(mIntent);
			}else if(v == sync){
				/*if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
					syncUploadData();
				}*/
			}else if(v == scanBarcode){
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
				}
				Intent mIntent = new Intent(BaseActivity.this, CameraOpenActivity.class);
				mIntent.putExtra("data","yes");
				startActivity(mIntent);
			}else if(v == nfcTapping){
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
				}
				Intent mIntent = new Intent(BaseActivity.this, NFCReadActivity.class);
				mIntent.putExtra("data","yes");
				startActivity(mIntent);
			}else if(v==logout){
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
				}
				Utils.clearPreferences(BaseActivity.this);

				TimeEmDbHandler dbHandler = new TimeEmDbHandler(BaseActivity.this);

				dbHandler.deleteNotificationsTable();
				dbHandler.deleteActiveUsers();
				dbHandler.deleteTaskTable();
				dbHandler.deleteTeamTable();

				Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				
			}else if(v == slider){
				
				v.startAnimation(buttonClick);
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
				}
				else{
					mDrawerLayout.openDrawer(flyoutDrawerRl);
				}
			}else if (v == myTasks) {
				setSelection(true, false, false, false);
				Intent intent = new Intent(BaseActivity.this, TaskListActivity.class);
				intent.putExtra("UserId", HomeActivity.user.getId());
				startActivity(intent);
			} else if (v == myTeam) {
				setSelection(false, true, false, false);
				Intent intent = new Intent(BaseActivity.this, UserListActivity.class);
				startActivity(intent);
			} else if (v == lay_notifications) {
				setSelection(false, false, true, false);
				Intent intent = new Intent(BaseActivity.this, NotificationListActivity.class);
				startActivity(intent);
			} else if (v == settings) {
				setSelection(false, false, false, true);
			}

		}
	};



	public void setSelection(Boolean isTaskSelected, Boolean isTeamSelected, Boolean isNotificationSelected, Boolean isSettingsSelected){
		myTasks.setBackgroundColor(getColor(isTaskSelected));
		myTeam.setBackgroundColor(getColor(isTeamSelected));
		lay_notifications.setBackgroundColor(getColor(isNotificationSelected));
		settings.setBackgroundColor(getColor(isSettingsSelected));
	}

	private int getColor(Boolean selected){
		if(selected)
			return resources.getColor(R.color.gradientBgEnd);
		else
			return resources.getColor(R.color.gradientBgStart);
	}
	
	private void setListenerOnDrawer(){
		mDrawerToggle=new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_launcher,
				R.string.app_name, R.string.app_name){
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};
	}
	
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		//we can handle other action bar items here later...

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setSelection(false, false, false, false);
	}

	@Override
	public void processFinish(String output, String methodName) {

	}

}
