package com.time_em.team;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

public class UserListActivity extends Activity implements AsyncResponseTimeEm{

	private ListView taskListview;
	private ArrayList<User> team;
	private Time_emJsonParser parser;
	private HorizontalScrollView sView;
	private TextView swipeInfo;
	
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
		sView = (HorizontalScrollView)findViewById(R.id.dateSlider);
		swipeInfo = (TextView)findViewById(R.id.swipeInfo);
		
		sView.setVisibility(View.GONE);
		swipeInfo.setVisibility(View.GONE);
	}
	
	private void getUserList(int userId){
		
		if (Utils.isNetworkAvailable(UserListActivity.this)) {
			
			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			postDataParameters.put("userId", String.valueOf(userId));
			
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					UserListActivity.this, "get", Utils.getTeamAPI,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) UserListActivity.this;
			mWebPageTask.execute();

		} else {
			Utils.alertMessage(UserListActivity.this, Utils.network_error);
		}
	}
	
	public class TeamAdapter extends BaseAdapter {
		private Context context;
		private TextView userName;
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.user_row, parent, false);
			    
			   
			}
			user = team.get(position);
		
			 userName = (TextView)convertView.findViewById(R.id.userName);
			    shift = (ImageView)convertView.findViewById(R.id.shiftInfo);
			    status = (ImageView)convertView.findViewById(R.id.status);
			    			
			    userName.setText(user.getFullName());
			    
			    if(user.isSignedIn())
			    	status.setImageResource(R.drawable.online);
			    else
			    	status.setImageResource(R.drawable.offline);
			    
			    if(user.isNightShift())
			    	shift.setImageResource(R.drawable.night);
			    else
			    	shift.setImageResource(R.drawable.day);
			    
			    return convertView;
		}		
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("output", output);
		team = parser.getTeamList(output, methodName);
		taskListview.setAdapter(new TeamAdapter(UserListActivity.this));
	}
}
