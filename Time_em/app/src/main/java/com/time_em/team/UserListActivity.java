package com.time_em.team;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.tasks.TaskListActivity;
import com.time_em.utils.Utils;

public class UserListActivity extends Activity implements AsyncResponseTimeEm{

	private ListView taskListview;
	private ArrayList<User> team;
	private Time_emJsonParser parser;
//	private HorizontalScrollView sView;
	private TextView swipeInfo, headerText;
	private ImageView back, addTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);
	
		initScreen();
		SimpleDateFormat postFormater = new SimpleDateFormat("dd/MM/yyyy"); 

		String currentDate = postFormater.format(new Date()); 
		getUserList(HomeActivity.user.getId());
	}
	
	private void initScreen(){
		taskListview = (ListView)findViewById(R.id.taskList);
		parser = new Time_emJsonParser(UserListActivity.this);
//		sView = (HorizontalScrollView)findViewById(R.id.dateSlider);
		swipeInfo = (TextView)findViewById(R.id.swipeInfo);
		back = (ImageView)findViewById(R.id.back);
		addTask = (ImageView) findViewById(R.id.AddButton);
		headerText = (TextView)findViewById(R.id.headerText);
//		sView.setVisibility(View.GONE);
		swipeInfo.setVisibility(View.GONE);
		addTask.setVisibility(View.GONE);
		headerText.setText("My Team");
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		taskListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(UserListActivity.this, TaskListActivity.class);
				intent.putExtra("UserId", team.get(position).getId());
				intent.putExtra("UserName", team.get(position).getFirstName());
				startActivity(intent);
			}
		});
	}
	
	private void getUserList(int userId){
		
		if (Utils.isNetworkAvailable(UserListActivity.this)) {
			String timeStamp = Utils.getSharedPrefs(UserListActivity.this, userId+getResources().getString(R.string.teamTimeStampStr));
			if(timeStamp==null || timeStamp.equals(null) || timeStamp.equals("null"))
				timeStamp="";
			
			HashMap<String, String> postDataParameters = new HashMap<String, String>();

			postDataParameters.put("TimeStamp",timeStamp);
			postDataParameters.put("UserId", String.valueOf(userId));

			Log.e("values","userid: "+String.valueOf(userId)+", TimeStamp: "+timeStamp);
			
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					UserListActivity.this, "post", Utils.getTeamAPI,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) UserListActivity.this;
			mWebPageTask.execute();

		} else {
			Utils.alertMessage(UserListActivity.this, Utils.network_error);
		}
	}

	public class TeamAdapter extends BaseSwipeAdapter {
		private Context context;
		private TextView userName, signInInfo, txtStatus, signOutInfo;
		private ImageView status, shift;
		private User user;

		public TeamAdapter(Context ctx) {
			context = ctx;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return team.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return team.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public void fillValues(final int position, View convertView) {
			// TODO Auto-generated method stub
			user = team.get(position);

			userName = (TextView)convertView.findViewById(R.id.userName);
			shift = (ImageView)convertView.findViewById(R.id.shiftInfo);
			status = (ImageView)convertView.findViewById(R.id.status);
			signInInfo = (TextView)convertView.findViewById(R.id.signInInfo);
			txtStatus = (TextView)convertView.findViewById(R.id.txtUserStatus);
			signOutInfo = (TextView)convertView.findViewById(R.id.signOutInfo);

			userName.setText(user.getFullName());

			if(user.isSignedIn()) {
				signOutInfo.setVisibility(View.GONE);
				status.setImageResource(R.drawable.online);
				signInInfo.setText("In: "+user.getSignInAt());
				txtStatus.setText("Sign Out");
			}else {
				status.setImageResource(R.drawable.offline);

				if(user.getSignInAt()==null || user.getSignInAt().equals("")) {
					signInInfo.setVisibility(View.GONE);
					signOutInfo.setVisibility(View.GONE);
				}else {
					signInInfo.setVisibility(View.VISIBLE);
					signOutInfo.setVisibility(View.VISIBLE);
					signInInfo.setText("In: " + user.getSignInAt());
					signOutInfo.setText("Out: " + user.getSignOutAt());
				}

				txtStatus.setText("Sign In");
			}

			if(user.isNightShift())
				shift.setImageResource(R.drawable.night);
			else
				shift.setImageResource(R.drawable.day);

			txtStatus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					user = team.get(position);

					if(user.isSignedIn()) {
						Utils.ChangeStatus(UserListActivity.this, "" + user.getId(), "signOut");
					}else{
						Utils.ChangeStatus(UserListActivity.this, "" + user.getId(), "signIn");
					}
				}
			});
		}

		@Override
		public View generateView(int arg0, ViewGroup arg1) {
			return LayoutInflater.from(UserListActivity.this).inflate(
					R.layout.user_row, null);

		}

		@Override
		public int getSwipeLayoutResourceId(int arg0) {
			// TODO Auto-generated method stub
			return R.id.swipe;
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		if(methodName.equals(Utils.getTeamAPI)) {
			Log.e("output", ",,, ::: " + output);
			ArrayList<User> teamMembers = parser.getTeamList(output, methodName);
			TimeEmDbHandler dbHandler = new TimeEmDbHandler(UserListActivity.this);
			dbHandler.updateTeam(teamMembers);

			team = dbHandler.getTeam(HomeActivity.user.getId());


			taskListview.setAdapter(new TeamAdapter(UserListActivity.this));
		}else{
//			Utils.showToast(UserListActivity.this, output);
			getUserList(HomeActivity.user.getId());
		}
	}
}