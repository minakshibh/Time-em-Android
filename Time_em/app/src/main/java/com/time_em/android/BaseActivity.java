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
import com.time_em.model.Notification;
import com.time_em.model.TaskEntry;
import com.time_em.notifications.NotificationListActivity;
import com.time_em.notifications.SendNotification;
import com.time_em.profile.MyProfileActivity;
import com.time_em.tasks.TaskListActivity;
import com.time_em.team.UserListActivity;
import com.time_em.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;


public class BaseActivity extends Activity{

	public ActionBarDrawerToggle mDrawerToggle;
	public DrawerLayout mDrawerLayout;
	public RelativeLayout flyoutDrawerRl;
	
	public AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
	public RelativeLayout contentFrame, profile, sync, scanBarcode, nfcTapping, logout;
	public RelativeLayout slider;
	public LinearLayout myTasks, myTeam, lay_notifications,settings;
	private Resources resources;
	public ImageView menuUserStatus,imageSync;
	private ArrayList<Notification> notifications=new ArrayList<>();
	private ArrayList<TaskEntry> tasks=new ArrayList<>();
	public static ArrayList<String> deleteIds=new ArrayList<>();
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
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
					syncUploadData();
				}
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

	private void syncUploadData() {

		TimeEmDbHandler dbHandler = new TimeEmDbHandler(BaseActivity.this);

		//for notification
		notifications.clear();
		notifications.addAll(dbHandler.getNotificationsByType("true", true));
		Log.e("notification size", "" + notifications.size());
		//for delete notification
		if (notifications != null && notifications.size() > 0) {

			//delete offline values
			//dbHandler.deleteNotificationOffline("true");
		}


		//for task
		tasks.clear();
		tasks.addAll(dbHandler.getTaskEnteries(HomeActivity.user.getId(),"true",true));
		syncUploadAPI(tasks,deleteIds);
		Log.e("task size", "" + tasks.size());
		// for delete task
		if (notifications != null && notifications.size() > 0) {

			//delete offline values
			//dbHandler.deleteNotificationOffline("true");
		}
	}

	private void syncUploadAPI(ArrayList<TaskEntry> tasks,ArrayList<String> deleteIds) {


		HashMap<String, String> parameters = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> arrayHashMap=new ArrayList<>();
		/*"TaskActivityId": 0,
				"CreatedDate": "06-17-2016",
				"Comments": "Add by Sync 1",
				"UserId": 2,
				"TaskId": 7102,
				"ActivityId": 28419,
				"UniqueNumber":1,
				"operation": "Add"*/
		for(int i=0;i<tasks.size();i++) {
			parameters.put("TaskActivityId", "" + tasks.get(i).getActivityId());
			parameters.put("CreatedDate", "" + tasks.get(i).getCreatedDate());
			parameters.put("Comments", "" + tasks.get(i).getComments());
			parameters.put("UserId", "" + tasks.get(i).getUserId());
			parameters.put("TaskId", "" + tasks.get(i).getTaskId());
			parameters.put("ActivityId", "" + tasks.get(i).getActivityId());
			parameters.put("UniqueNumber", "" + tasks.get(i).getAttachmentImageFile());
			Log.e("getAttachmentImageFile", "" + tasks.get(i).getAttachmentImageFile());
			if(tasks.get(i).getId()==0)
			parameters.put("operation", "add" );
			else
			parameters.put("operation", "update" );
			arrayHashMap.add(parameters);
		}
		for(int i=0;i<deleteIds.size();i++) {
			parameters.put("Id", "" + deleteIds.get(i));
			parameters.put("operation", "" + "delete");
			arrayHashMap.add(parameters);
		}





		//Log.e("hash", "" + arrayHashMap.toString());

		if (Utils.isNetworkAvailable(BaseActivity.this)) {
			HashMap<String, String> postDataParameters = new HashMap<String, String>();

			   postDataParameters.put("userTaskActivities", arrayHashMap.toString());
				//timeemapi.azurewebsites.net/api/UserActivity/Sync
				Log.e(Utils.Sync,postDataParameters.toString());
				AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
						BaseActivity.this, "post", Utils.Sync,
						postDataParameters, true, "Please wait...");
				mWebPageTask.delegate = (AsyncResponseTimeEm) BaseActivity.this;
				mWebPageTask.execute();

			} else {
				Utils.alertMessage(BaseActivity.this, Utils.network_error);
			}


	}

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
}
